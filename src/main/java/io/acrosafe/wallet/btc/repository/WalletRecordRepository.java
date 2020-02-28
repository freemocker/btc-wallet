package io.acrosafe.wallet.btc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.acrosafe.wallet.btc.domain.WalletRecord;

public interface WalletRecordRepository extends JpaRepository<WalletRecord, String>
{
    public List<WalletRecord> findAllByEnabledTrue();
}
