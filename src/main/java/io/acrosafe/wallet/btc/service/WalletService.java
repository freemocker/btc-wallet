/**
 * MIT License
 *
 * Copyright (c) 2020 acrosafe technologies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.acrosafe.wallet.btc.service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.MarriedKeyChain;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableList;

import io.acrosafe.wallet.btc.config.ApplicationProperties;
import io.acrosafe.wallet.btc.domain.AddressRecord;
import io.acrosafe.wallet.btc.domain.TransactionOutputRecord;
import io.acrosafe.wallet.btc.domain.TransactionRecord;
import io.acrosafe.wallet.btc.domain.WalletRecord;
import io.acrosafe.wallet.btc.exception.CryptoException;
import io.acrosafe.wallet.btc.exception.InvalidCoinSymbolException;
import io.acrosafe.wallet.btc.exception.InvalidPassphraseException;
import io.acrosafe.wallet.btc.exception.InvalidSymbolException;
import io.acrosafe.wallet.btc.exception.ServiceNotReadyException;
import io.acrosafe.wallet.btc.exception.WalletNotFoundException;
import io.acrosafe.wallet.btc.repository.AddressRecordRepository;
import io.acrosafe.wallet.btc.repository.TransactionOutputRecordRepository;
import io.acrosafe.wallet.btc.repository.TransactionRecordRepository;
import io.acrosafe.wallet.btc.repository.WalletRecordRepository;
import io.acrosafe.wallet.btc.util.CryptoUtils;
import io.acrosafe.wallet.btc.util.Passphrase;
import io.acrosafe.wallet.core.btc.BTCTransaction;
import io.acrosafe.wallet.core.btc.BlockChainNetwork;
import io.acrosafe.wallet.core.btc.MultisigWallet;
import io.acrosafe.wallet.core.btc.MultisigWalletBalance;
import io.acrosafe.wallet.core.btc.SeedGenerator;
import io.acrosafe.wallet.core.btc.TransactionStatus;
import io.acrosafe.wallet.core.btc.TransactionType;
import io.acrosafe.wallet.core.btc.util.IDGenerator;
import io.acrosafe.wallet.core.btc.util.WalletUtils;

@Service
public class WalletService
{
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    private static final ImmutableList<ChildNumber> BIP44_ACCOUNT_BTC_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(0, true), ChildNumber.ZERO_HARDENED);

    // default m-of-n value. m = 2, n = 3
    private static final Integer DEFAULT_NUMBER_OF_SIGNER = 2;
    private static final Integer DEFAULT_NUMBER_OF_BLOCK = 2;

    // Security strength
    private static final Integer SECURITY_STRENGTH = 256;

    // BTC symbol
    private static final String COIN_SYMBOL = "BTC";

    @Autowired
    private SeedGenerator seedGenerator;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private NetworkParameters networkParameters;

    @Autowired
    private BlockChainNetwork blockChainNetwork;

    @Autowired
    private WalletRecordRepository walletRecordRepository;

    @Autowired
    private AddressRecordRepository addressRecordRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private TransactionOutputRecordRepository transactionOutputRecordRepository;

    private boolean isServiceReady;

    private Map<String, BTCTransaction> pendingTransactionCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initialize()
    {
        try
        {
            blockChainNetwork.initializeBlockChainNetwork(createDownloadProgressListener());
            restoreWallets();
            blockChainNetwork.downloadBlockChainData();
        }
        catch (Throwable t)
        {
            logger.error("failed to start BTC wallet service.", t);
        }
    }

    @Transactional
    public synchronized WalletRecord createWallet(String symbol, String label, Passphrase signingKeyPassphrase,
            Passphrase backupSigningKeyPassphrase, Boolean enabled)
            throws ServiceNotReadyException, InvalidSymbolException, InvalidPassphraseException, CryptoException
    {
        if (!isServiceReady)
        {
            throw new ServiceNotReadyException("downloading blockchain data. service is not available now.");
        }

        if (StringUtils.isEmpty(symbol) || !symbol.equalsIgnoreCase(COIN_SYMBOL))
        {
            throw new InvalidSymbolException("coin symbol is not valid.");
        }

        if (signingKeyPassphrase == null || StringUtils.isEmpty(signingKeyPassphrase.getStringValue()))
        {
            throw new InvalidPassphraseException("signing key passphrase cannot be empty or null.");
        }

        if (backupSigningKeyPassphrase == null || StringUtils.isEmpty(backupSigningKeyPassphrase.getStringValue()))
        {
            throw new InvalidPassphraseException("backup signing key passphrase cannot be empty or null.");
        }

        if (enabled == null)
        {
            enabled = true;
        }

        final String id = IDGenerator.randomUUID().toString();
        final Instant createdDate = Instant.now();
        final long creationTimeInSeconds = System.currentTimeMillis() / 1000;

        // Generates seeds
        final String serviceId = this.applicationProperties.getServiceId();
        final int entropyBits = this.applicationProperties.getEntropyBits();
        final byte[] ownerSeed = this.seedGenerator.getSeed(serviceId, SECURITY_STRENGTH, entropyBits);
        final byte[] marriedSeed = this.seedGenerator.getSeed(serviceId, SECURITY_STRENGTH, entropyBits);
        final byte[] signerSeed = this.seedGenerator.getSeed(serviceId, SECURITY_STRENGTH, entropyBits);
        final byte[] backupSignerSeed = this.seedGenerator.getSeed(serviceId, SECURITY_STRENGTH, entropyBits);

        DeterministicSeed deterministicSeed = this.seedGenerator.restoreDeterministicSeed(ownerSeed, StringUtils.EMPTY,
                MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);

        DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(deterministicSeed)
                .outputScriptType(Script.ScriptType.P2WPKH).accountPath(BIP44_ACCOUNT_BTC_PATH).build();
        KeyChainGroup keyChainGroup = KeyChainGroup.builder(networkParameters).addChain(chain).build();

        List<DeterministicKey> watchingKeys = new ArrayList<>();

        // signer key
        DeterministicSeed signerDeterministicSeed =
                this.seedGenerator.restoreDeterministicSeed(signerSeed, StringUtils.EMPTY, creationTimeInSeconds);

        final DeterministicKeyChain signerKeyChain = DeterministicKeyChain.builder().seed(signerDeterministicSeed).build();
        final String signerKeyString = signerKeyChain.getWatchingKey().serializePubB58(networkParameters);
        DeterministicKey signerKey = DeterministicKey.deserializeB58(null, signerKeyString, networkParameters);

        // backup signer key
        DeterministicSeed backupSignerDeterministicSeed =
                this.seedGenerator.restoreDeterministicSeed(backupSignerSeed, StringUtils.EMPTY, creationTimeInSeconds);

        final DeterministicKeyChain backupSignerKeyChain =
                DeterministicKeyChain.builder().seed(backupSignerDeterministicSeed).build();
        final String backupSignerKeyString = backupSignerKeyChain.getWatchingKey().serializePubB58(networkParameters);
        DeterministicKey backupSignerKey = DeterministicKey.deserializeB58(null, backupSignerKeyString, networkParameters);

        watchingKeys.add(signerKey);
        watchingKeys.add(backupSignerKey);

        DeterministicSeed deterministicMarriedSeed =
                this.seedGenerator.restoreDeterministicSeed(marriedSeed, StringUtils.EMPTY, creationTimeInSeconds);
        MarriedKeyChain marriedKeyChain = MarriedKeyChain.builder().seed(deterministicMarriedSeed).followingKeys(watchingKeys)
                .threshold(DEFAULT_NUMBER_OF_SIGNER).build();

        MultisigWallet wallet = new MultisigWallet(id, networkParameters, keyChainGroup);
        wallet.addWalletListeners(new BTCWalletCoinsSentEventListener(), new BTCWalletCoinsReceivedEventListener());

        wallet.addAndActivateHDChain(marriedKeyChain);

        final byte[] ownerSpec = CryptoUtils.generateIVParameterSpecBytes();
        final String encodedOwnerSpec = Base64.getEncoder().encodeToString(ownerSpec);
        final byte[] ownerSalt = CryptoUtils.generateSaltBytes();
        final String encodedOwnerSalt = Base64.getEncoder().encodeToString(ownerSalt);

        final byte[] signerSpec = CryptoUtils.generateIVParameterSpecBytes();
        final String encodedSignerSpec = Base64.getEncoder().encodeToString(signerSpec);
        final byte[] signerSalt = CryptoUtils.generateSaltBytes();
        final String encodedSignerSalt = Base64.getEncoder().encodeToString(signerSalt);

        String encryptedOwnerSeed = null;
        String encryptedMarriedSeed = null;
        String encryptedSignerSeed = null;
        String encryptedBackupSignerSeed = null;

        try
        {
            Passphrase systemPassphrase = applicationProperties.getPassphrase();
            encryptedOwnerSeed = CryptoUtils.encrypt(systemPassphrase.getStringValue(), ownerSeed, ownerSpec, ownerSalt);
            encryptedMarriedSeed = CryptoUtils.encrypt(systemPassphrase.getStringValue(), marriedSeed, ownerSpec, ownerSalt);
            encryptedSignerSeed = CryptoUtils.encrypt(signingKeyPassphrase.getStringValue(), signerSeed, signerSpec, signerSalt);
            encryptedBackupSignerSeed =
                    CryptoUtils.encrypt(backupSigningKeyPassphrase.getStringValue(), backupSignerSeed, signerSpec, signerSalt);
        }
        catch (Throwable t)
        {
            // this shouldn't happen at all.
            throw new CryptoException("Invalid crypto operation.", t);
        }

        WalletRecord walletRecord = new WalletRecord();
        walletRecord.setId(id);
        walletRecord.setBackupSignerSeed(encryptedBackupSignerSeed);
        walletRecord.setBackupSignerWatchingKey(backupSignerKeyString);
        walletRecord.setSignerSeed(encryptedSignerSeed);
        walletRecord.setSignerWatchingKey(signerKeyString);
        walletRecord.setSignerSpec(encodedSignerSpec);
        walletRecord.setSignerSalt(encodedSignerSalt);
        walletRecord.setOwnerSeed(encryptedOwnerSeed);
        walletRecord.setMarriedSeed(encryptedMarriedSeed);
        walletRecord.setOwnerSpec(encodedOwnerSpec);
        walletRecord.setOwnerSalt(encodedOwnerSalt);
        walletRecord.setCreatedDate(createdDate);
        walletRecord.setEnabled(enabled);
        walletRecord.setLabel(label);
        walletRecord.setSeedTimestamp(creationTimeInSeconds);

        walletRecordRepository.save(walletRecord);
        this.blockChainNetwork.addWallet(wallet);

        logger.info("new multisig wallet created. id = {}, createdDate = {}", id, createdDate);

        return walletRecord;
    }

    @Transactional
    public MultisigWalletBalance getBalance(String walletId) throws ServiceNotReadyException, WalletNotFoundException
    {
        if (!isServiceReady)
        {
            throw new ServiceNotReadyException("downloading blockchain data. service is not available now.");
        }

        MultisigWallet wallet = this.blockChainNetwork.getWallet(walletId);
        if (wallet == null)
        {
            throw new WalletNotFoundException("wallet doesn't exist. id = " + walletId);
        }

        MultisigWalletBalance balance = wallet.getWalletBalance();
        return balance;
    }

    @Transactional
    public List<TransactionRecord> getTransactions(String walletId, int pageId, int size)
            throws ServiceNotReadyException, WalletNotFoundException
    {
        if (!isServiceReady)
        {
            throw new ServiceNotReadyException("downloading blockchain data. service is not available now.");
        }

        MultisigWallet wallet = this.blockChainNetwork.getWallet(walletId);
        if (wallet == null)
        {
            throw new WalletNotFoundException("failed to find wallet. id = " + walletId);
        }

        Pageable pageable = PageRequest.of(pageId, size, Sort.by(Sort.Direction.ASC, "CreatedDate"));
        List<TransactionRecord> records = this.transactionRecordRepository.findAllByWalletId(walletId, pageable);

        return records;
    }

    @Transactional
    public WalletRecord getWallet(String walletId) throws WalletNotFoundException, ServiceNotReadyException
    {
        if (!isServiceReady)
        {
            throw new ServiceNotReadyException("downloading blockchain data. service is not available now.");
        }

        WalletRecord walletRecord = this.walletRecordRepository.findById(walletId).orElse(null);
        MultisigWallet wallet = this.blockChainNetwork.getWallet(walletId);
        if (wallet == null || walletRecord == null)
        {
            throw new WalletNotFoundException("wallet doesn't exist. id = " + walletId);
        }

        return walletRecord;
    }

    @Transactional
    public List<WalletRecord> getWallets(int pageId, int size) throws ServiceNotReadyException
    {
        if (!isServiceReady)
        {
            throw new ServiceNotReadyException("downloading blockchain data. service is not available now.");
        }

        Pageable pageable = PageRequest.of(pageId, size, Sort.by(Sort.Direction.DESC, "CreatedDate"));
        Page<WalletRecord> records = this.walletRecordRepository.findAll(pageable);

        return records.toList();
    }

    @Transactional
    public synchronized AddressRecord refreshReceivingAddress(String walletId, String coinSymbol, String label)
            throws WalletNotFoundException, ServiceNotReadyException, InvalidCoinSymbolException
    {
        if (!isServiceReady)
        {
            throw new ServiceNotReadyException("downloading blockchain data. service is not available now.");
        }

        MultisigWallet wallet = this.blockChainNetwork.getWallet(walletId);
        if (wallet == null)
        {
            throw new WalletNotFoundException("failed to find wallet. id = " + walletId);
        }

        if (StringUtils.isEmpty(coinSymbol) || !coinSymbol.equalsIgnoreCase(COIN_SYMBOL))
        {
            throw new InvalidCoinSymbolException("coin symbol is not valid.");
        }

        Address address = wallet.freshReceiveAddress();
        AddressRecord record = this.addressRecordRepository.findById(address.toString()).orElse(null);
        while (record != null)
        {
            address = wallet.freshReceiveAddress();
            record = this.addressRecordRepository.findById(address.toString()).orElse(null);
        }

        logger.info("new receiving address generated. address = {}", address.toString());
        wallet.addWatchedAddress(address);

        AddressRecord addressRecord = new AddressRecord();
        addressRecord.setCreatedDate(Instant.now());
        addressRecord.setWalletId(walletId);
        addressRecord.setChangeAddress(false);
        addressRecord.setReceiveAddress(address.toString());
        addressRecord.setLabel(label);

        this.addressRecordRepository.save(addressRecord);

        return addressRecord;
    }

    @Transactional
    public void updateTransaction(TransactionConfidence confidence)
    {
        TransactionConfidence.ConfidenceType type = confidence.getConfidenceType();
        switch (type)
        {
        case BUILDING:
        {
            if (confidence.getDepthInBlocks() >= this.applicationProperties.getDepositConfirmationNumber())
            {
                final String transactionId = confidence.getTransactionHash().toString();
                TransactionRecord transactionRecord =
                        transactionRecordRepository.findFirstByTransactionId(transactionId).orElse(null);

                if (transactionRecord != null)
                {
                    transactionRecord.setStatus(TransactionStatus.CONFIRMED);
                    transactionRecord.setLastModifiedDate(Instant.now());

                    transactionRecordRepository.save(transactionRecord);

                    // remove transaction id
                    BTCTransaction transaction = pendingTransactionCache.get(transactionId);
                    if (transaction != null)
                    {
                        pendingTransactionCache.get(transactionId).removeTransactionConfidenceListener();
                        pendingTransactionCache.remove(transactionId);
                    }
                }
            }
            break;
        }
        case PENDING:
        {
            break;
        }
        case IN_CONFLICT:
        case DEAD:
        case UNKNOWN:
        default:
        {
            final String transactionId = confidence.getTransactionHash().toString();
            TransactionRecord transactionRecord =
                    transactionRecordRepository.findFirstByTransactionId(transactionId).orElse(null);

            if (transactionRecord != null)
            {
                transactionRecord.setStatus(TransactionStatus.FAILED);
                transactionRecord.setLastModifiedDate(Instant.now());

                transactionRecordRepository.save(transactionRecord);

                // remove transaction id
                pendingTransactionCache.get(transactionId).removeTransactionConfidenceListener();
                pendingTransactionCache.remove(transactionId);
            }
        }
        }

    }

    private DownloadProgressTracker createDownloadProgressListener() throws BlockStoreException
    {
        return new DownloadProgressTracker()
        {
            @Override
            public void progress(double pct, int blocksSoFar, Date date)
            {
                super.progress(pct, blocksSoFar, date);
                // only print out 25%, 50% 75% 100% percentage
                isServiceReady = false;

                int remainder = ((int) pct) % 25;
                if (remainder == 0)
                {
                    logger.info("downloading blockchain data now. percentage = {}%, blockNumber = {}, date = {}", (int) pct,
                            blocksSoFar, date);
                }
            }

            @Override
            public void doneDownload()
            {
                logger.info("All blocks have been downloaded. BTC wallet service is available.");
                isServiceReady = true;

                restorePendingTransactions();
            }
        };
    }

    private void restorePendingTransactions()
    {
        List<TransactionRecord> transactionRecords =
                this.transactionRecordRepository.findAllByStatus(TransactionStatus.UNCONFIRMED);

        List<TransactionRecord> updatedRecords = new ArrayList<>();
        for (TransactionRecord transactionRecord : transactionRecords)
        {
            MultisigWallet wallet = this.blockChainNetwork.getWallet(transactionRecord.getWalletId());
            Transaction transaction =
                    wallet.getTransaction(Sha256Hash.wrap(Utils.HEX.decode(transactionRecord.getTransactionId())));

            if (transaction == null)
            {
                logger.warn("transaction {} is not in wallet cache.", transactionRecord.getTransactionId());
            }
            else
            {
                final TransactionConfidence.ConfidenceType type = transaction.getConfidence().getConfidenceType();
                final int depth = transaction.getConfidence().getDepthInBlocks();

                if (type == TransactionConfidence.ConfidenceType.PENDING || (type == TransactionConfidence.ConfidenceType.BUILDING
                        && (depth < applicationProperties.getDepositConfirmationNumber())))
                {
                    BTCTransaction btcTransaction = new BTCTransaction(wallet.getWalletId(), transaction);
                    btcTransaction.addTransactionConfidenceListener(new BTCTransactionConfidenceEventListener());

                    pendingTransactionCache.put(btcTransaction.getTransactionId(), btcTransaction);
                }
                else
                {
                    transactionRecord.setStatus(
                            WalletUtils.getBlockChainTransactionStatus(type, transaction.getConfidence().getDepthInBlocks(),
                                    applicationProperties.getDepositConfirmationNumber()));
                    transactionRecord.setLastModifiedDate(Instant.now());
                    updatedRecords.add(transactionRecord);
                }
            }

            logger.info("restore all the pending transactions. size = {}", pendingTransactionCache.size());

            if (updatedRecords != null && updatedRecords.size() != 0)
            {
                transactionRecordRepository.saveAll(updatedRecords);
            }
        }
    }

    private void restoreWallets() throws CryptoException
    {
        List<WalletRecord> walletRecords = this.walletRecordRepository.findAllByEnabledTrue();
        if (walletRecords != null && walletRecords.size() != 0)
        {
            for (WalletRecord walletRecord : walletRecords)
            {
                try
                {
                    Passphrase systemPassphrase = applicationProperties.getPassphrase();

                    final String id = walletRecord.getId();
                    final long creationTimeInSeconds = walletRecord.getSeedTimestamp();
                    final String encryptedOwnerSeed = walletRecord.getOwnerSeed();
                    final String encryptedMarriedSeed = walletRecord.getMarriedSeed();

                    final byte[] ownerSalt = Base64.getDecoder().decode(walletRecord.getOwnerSalt());
                    final byte[] ownerSpec = Base64.getDecoder().decode(walletRecord.getOwnerSpec());

                    final byte[] ownerSeed =
                            CryptoUtils.decrypt(systemPassphrase.getStringValue(), encryptedOwnerSeed, ownerSpec, ownerSalt);
                    final byte[] marriedSeed =
                            CryptoUtils.decrypt(systemPassphrase.getStringValue(), encryptedMarriedSeed, ownerSpec, ownerSalt);

                    DeterministicSeed deterministicOwnerSeed =
                            this.seedGenerator.restoreDeterministicSeed(ownerSeed, StringUtils.EMPTY, creationTimeInSeconds);
                    DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(deterministicOwnerSeed)
                            .outputScriptType(Script.ScriptType.P2WPKH).accountPath(BIP44_ACCOUNT_BTC_PATH).build();
                    KeyChainGroup keyChainGroup = KeyChainGroup.builder(networkParameters).addChain(chain).build();

                    List<DeterministicKey> watchingKeys = new ArrayList<>();

                    // signer key
                    DeterministicKey signerKey =
                            DeterministicKey.deserializeB58(null, walletRecord.getSignerWatchingKey(), networkParameters);
                    DeterministicKey backupSignerKey =
                            DeterministicKey.deserializeB58(null, walletRecord.getBackupSignerWatchingKey(), networkParameters);

                    watchingKeys.add(signerKey);
                    watchingKeys.add(backupSignerKey);

                    DeterministicSeed deterministicMarriedSeed =
                            this.seedGenerator.restoreDeterministicSeed(marriedSeed, StringUtils.EMPTY, creationTimeInSeconds);
                    MarriedKeyChain marriedKeyChain = MarriedKeyChain.builder().seed(deterministicMarriedSeed)
                            .followingKeys(watchingKeys).threshold(DEFAULT_NUMBER_OF_SIGNER).build();

                    MultisigWallet wallet = new MultisigWallet(id, networkParameters, keyChainGroup);
                    wallet.addWalletListeners(new BTCWalletCoinsSentEventListener(), new BTCWalletCoinsReceivedEventListener());
                    wallet.addAndActivateHDChain(marriedKeyChain);

                    List<AddressRecord> addressRecords = this.addressRecordRepository.findAllByWalletId(id);
                    if (addressRecords != null && addressRecords.size() != 0)
                    {
                        for (AddressRecord addressRecord : addressRecords)
                        {
                            final Address address = Address.fromString(networkParameters, addressRecord.getReceiveAddress());
                            wallet.addWatchedAddress(address);
                        }
                    }

                    this.blockChainNetwork.addWallet(wallet);
                }
                catch (Throwable t)
                {
                    // this shouldn't happen at all.
                    throw new CryptoException("Invalid crypto operation.", t);
                }
            }
        }
    }

    /**
     * Implementation of coins received event listener
     */
    public class BTCWalletCoinsReceivedEventListener implements WalletCoinsReceivedEventListener
    {

        @Override
        public void onCoinsReceived(Wallet wallet, Transaction transaction, Coin prevBalance, Coin newBalance)
        {
            final String walletId = wallet.getDescription();
            final Coin diff = newBalance.subtract(prevBalance);
            final String transactionId = transaction.getTxId().toString();

            if (diff.isGreaterThan(Coin.ZERO))
            {
                logger.info("new deposite received. transactionId = {}, walletId = {}, amount = {}", transactionId, walletId,
                        diff);

                TransactionRecord transactionRecord =
                        transactionRecordRepository.findFirstByTransactionId(transactionId).orElse(null);

                // If record is not saved in db
                if (transactionRecord == null)
                {
                    final TransactionConfidence.ConfidenceType confidenceType = transaction.getConfidence().getConfidenceType();
                    final int depthInBlocks = transaction.getConfidence().getDepthInBlocks();

                    TransactionStatus status = WalletUtils.getBlockChainTransactionStatus(confidenceType, depthInBlocks,
                            applicationProperties.getDepositConfirmationNumber());

                    logger.info("transaction {} is not in DB. confidencyType = {}, depthInBlocks = {}", transactionId,
                            confidenceType, depthInBlocks);
                    TransactionRecord record = new TransactionRecord();
                    record.setId(IDGenerator.randomUUID().toString());
                    record.setTransactionId(transactionId);
                    record.setLastModifiedDate(transaction.getUpdateTime().toInstant());
                    record.setWalletId(walletId);
                    record.setFee(BigInteger.ZERO);
                    record.setTransactionType(TransactionType.DEPOSIT);
                    record.setStatus(status);
                    if (!StringUtils.isEmpty(transaction.getMemo()))
                    {
                        record.setMemo(transaction.getMemo());
                    }

                    List<TransactionOutput> outputs = transaction.getOutputs();
                    if (outputs != null && outputs.size() != 0)
                    {
                        for (TransactionOutput output : outputs)
                        {
                            if (output.isMineOrWatched(wallet))
                            {
                                final String address = output.getScriptPubKey().getToAddress(networkParameters).toString();
                                final int index = output.getIndex();
                                final long amount = output.getValue().longValue();
                                logger.info(
                                        "transaction record doesn't exist. adding output to transaction record. address = {}, index = {}, value = {}",
                                        address, index, amount);
                                TransactionOutputRecord transactionOutputRecord = new TransactionOutputRecord();
                                transactionOutputRecord.setId(IDGenerator.randomUUID().toString());
                                transactionOutputRecord.setAmount(BigInteger.valueOf(amount));
                                transactionOutputRecord.setCreatedDate(Instant.now());
                                transactionOutputRecord.setOutputIndex(index);
                                transactionOutputRecord.setTransactionId(record.getId());
                                transactionOutputRecord.setDestination(address);

                                record.addOutput(transactionOutputRecord);
                            }
                        }
                    }

                    transactionRecordRepository.save(record);

                    if (status == TransactionStatus.UNCONFIRMED && !pendingTransactionCache.containsKey(transactionId))
                    {
                        BTCTransaction btcTransaction = new BTCTransaction(wallet.getDescription(), transaction);
                        btcTransaction.addTransactionConfidenceListener(new BTCTransactionConfidenceEventListener());

                        pendingTransactionCache.put(transactionId, btcTransaction);
                    }
                }
                else
                {
                    logger.info("transaction record found in DB. transactionId = {}, walletId = {}, balance = {}", transactionId,
                            walletId, diff);
                    List<TransactionOutput> outputs = transaction.getOutputs();
                    if (outputs != null && outputs.size() != 0)
                    {
                        final String internalTransactionId = transactionRecord.getId();
                        for (TransactionOutput output : outputs)
                        {
                            if (output.isMineOrWatched(wallet))
                            {
                                final int index = output.getIndex();
                                final TransactionOutputRecord existingTransactionOutputRecord = transactionOutputRecordRepository
                                        .findFirstByTransactionIdAndOutputIndex(internalTransactionId, index).orElse(null);

                                final String address = output.getScriptPubKey().getToAddress(networkParameters).toString();
                                final long amount = output.getValue().longValue();
                                if (existingTransactionOutputRecord == null)
                                {
                                    logger.info(
                                            "transaction output doesn't exist. adding output to transaction record. address = {}, index = {}, value = {}",
                                            address, index, amount);
                                    TransactionOutputRecord transactionOutputRecord = new TransactionOutputRecord();
                                    transactionOutputRecord.setId(IDGenerator.randomUUID().toString());
                                    transactionOutputRecord.setAmount(BigInteger.valueOf(amount));
                                    transactionOutputRecord.setCreatedDate(Instant.now());
                                    transactionOutputRecord.setOutputIndex(index);
                                    transactionOutputRecord.setTransactionId(internalTransactionId);
                                    transactionOutputRecord.setDestination(address);

                                    transactionOutputRecordRepository.save(transactionOutputRecord);
                                }
                                else
                                {
                                    logger.info("transaction output already existed. address = {}, index = {}, value = {}",
                                            address, index, amount);
                                }
                            }
                        }
                    }

                    if (transactionRecord.getStatus() == TransactionStatus.UNCONFIRMED)
                    {
                        if (!pendingTransactionCache.containsKey(transactionId))
                        {
                            BTCTransaction btcTransaction = new BTCTransaction(wallet.getDescription(), transaction);
                            btcTransaction.addTransactionConfidenceListener(new BTCTransactionConfidenceEventListener());

                            pendingTransactionCache.put(transactionId, btcTransaction);
                        }
                    }
                }
            }
            else
            {
                logger.debug("diff {} in transaction {} is smaller than 0.", diff, transactionId);
            }
        }

    }

    public class BTCWalletCoinsSentEventListener implements WalletCoinsSentEventListener
    {
        @Override
        public void onCoinsSent(Wallet wallet, Transaction transaction, Coin prevBalance, Coin newBalance)
        {
            final String walletId = wallet.getDescription();
            final Coin diff = newBalance.subtract(prevBalance);
            final String transactionId = transaction.getTxId().toString();
            logger.info("new withdrawal received. transactionId = {}, walletId = {}, balance = {}, memo = {}, fee = {}",
                    transactionId, walletId, diff, transaction.getMemo(), transaction.getFee());
        }
    }

    public class BTCTransactionConfidenceEventListener implements TransactionConfidence.Listener
    {
        @Override
        public void onConfidenceChanged(TransactionConfidence confidence, ChangeReason reason)
        {
            updateTransaction(confidence);
        }
    }
}
