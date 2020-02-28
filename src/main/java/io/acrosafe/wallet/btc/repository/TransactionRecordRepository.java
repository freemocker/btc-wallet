package io.acrosafe.wallet.btc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import io.acrosafe.wallet.btc.common.TransactionStatus;
import io.acrosafe.wallet.btc.domain.TransactionRecord;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long>
{
    List<TransactionRecord> findAllByStatus(TransactionStatus status);

    List<TransactionRecord> findAllByWalletId(String walletId, Pageable pageable);

    List<TransactionRecord> findAllByInternalId(String internalId);

    Optional<TransactionRecord> findFirstByTransactionId(String transactionId);
}
