package io.acrosafe.wallet.btc.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.acrosafe.wallet.btc.domain.WalletRecord;
import io.acrosafe.wallet.btc.exception.CryptoException;
import io.acrosafe.wallet.btc.exception.InvalidPassphraseException;
import io.acrosafe.wallet.btc.exception.InvalidSymbolException;
import io.acrosafe.wallet.btc.exception.ServiceNotReadyException;
import io.acrosafe.wallet.btc.service.WalletService;
import io.acrosafe.wallet.btc.web.rest.request.CreateWalletRequest;
import io.acrosafe.wallet.btc.web.rest.response.CreateWalletResponse;
import io.acrosafe.wallet.btc.web.rest.response.Result;

@Controller
@RequestMapping("/api/v1/btc/wallet")
public class WalletResources
{
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(WalletResources.class);

    @Autowired
    private WalletService service;

    @PostMapping("/new")
    public ResponseEntity<CreateWalletResponse> createWallet(@RequestBody CreateWalletRequest request)
    {
        CreateWalletResponse response = new CreateWalletResponse();
        try
        {
            WalletRecord record = this.service.createWallet(request.getSymbol(), request.getLabel(),
                    request.getSigningKeyPassphrase(), request.getBackupSigningKeyPassphrase(), request.getEnabled());
            response.setCreatedDate(record.getCreatedDate());
            response.setEnabled(record.isEnabled());
            response.setId(record.getId());
            response.setEncryptedSigningKey(record.getSignerSeed());
            response.setEncryptedBackupKey(record.getBackupSignerSeed());
            response.setSpec(record.getSignerSpec());
            response.setSalt(record.getSignerSalt());
            response.setCreationTime(record.getSeedTimestamp());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (ServiceNotReadyException e)
        {
            response.setResultCode(Result.SERVICE_NOT_READY.getCode());
            response.setResult(Result.SERVICE_NOT_READY);
            return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
        catch (InvalidSymbolException e)
        {
            response.setResultCode(Result.INVALID_COIN_SYMBOL.getCode());
            response.setResult(Result.INVALID_COIN_SYMBOL);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        catch (CryptoException e)
        {
            response.setResultCode(Result.INVALID_CRYPTO_OPERATION.getCode());
            response.setResult(Result.INVALID_CRYPTO_OPERATION);
            return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
        catch (InvalidPassphraseException e)
        {
            response.setResultCode(Result.INVALID_PASSPHRASE.getCode());
            response.setResult(Result.INVALID_PASSPHRASE);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        catch (Throwable e)
        {
            logger.error("failed to create new wallet.", e);
            response.setResultCode(Result.UNKNOWN_ERROR.getCode());
            response.setResult(Result.UNKNOWN_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
