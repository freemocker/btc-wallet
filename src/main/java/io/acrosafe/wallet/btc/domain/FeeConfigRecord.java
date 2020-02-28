package io.acrosafe.wallet.btc.domain;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "fee_config_record")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FeeConfigRecord implements Serializable
{
    @Id
    @Column(name = "number_of_block", nullable = false)
    private Integer numberOfBlock;

    @Column(name = "fee_per_kb", nullable = false)
    private BigInteger feePerKb;

    public Integer getNumberOfBlock()
    {
        return numberOfBlock;
    }

    public void setNumberOfBlock(Integer numberOfBlock)
    {
        this.numberOfBlock = numberOfBlock;
    }

    public BigInteger getFeePerKb()
    {
        return feePerKb;
    }

    public void setFeePerKb(BigInteger feePerKb)
    {
        this.feePerKb = feePerKb;
    }
}