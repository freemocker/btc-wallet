package io.acrosafe.wallet.btc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.acrosafe.wallet.btc.domain.FeeConfigRecord;

public interface FeeConfigRecordRepository extends JpaRepository<FeeConfigRecord, Integer>
{
}
