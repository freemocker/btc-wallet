package io.acrosafe.wallet.btc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.acrosafe.wallet.btc.domain.TransactionOutputRecord;

public interface TransactionOutputRecordRepository extends JpaRepository<TransactionOutputRecord, Long>
{
    List<TransactionOutputRecord> findAllByTransactionId(String transactionId);
    Optional<TransactionOutputRecord> findFirstByTransactionIdAndOutputIndex(String transactionId, Integer outputIndex);

}
