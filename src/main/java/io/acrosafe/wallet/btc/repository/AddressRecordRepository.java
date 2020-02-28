package io.acrosafe.wallet.btc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.acrosafe.wallet.btc.domain.AddressRecord;

@Repository
public interface AddressRecordRepository extends JpaRepository<AddressRecord, String>
{
    public List<AddressRecord> findAllByWalletId(String walletId);

}
