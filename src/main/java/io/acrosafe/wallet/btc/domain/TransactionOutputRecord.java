package io.acrosafe.wallet.btc.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "transaction_output_record")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TransactionOutputRecord implements Serializable
{
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "transaction_id", nullable = true)
    private String transactionId;

    @Column(name = "output_index", nullable = true)
    private Integer outputIndex;

    @Column(name = "amount", nullable = false)
    private BigInteger amount;

    @Column(name = "destination", nullable = false)
    private String destination;

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private Instant createdDate = Instant.now();

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public BigInteger getAmount()
    {
        return amount;
    }

    public void setAmount(BigInteger amount)
    {
        this.amount = amount;
    }

    public Instant getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate)
    {
        this.createdDate = createdDate;
    }

    public Integer getOutputIndex()
    {
        return outputIndex;
    }

    public void setOutputIndex(Integer outputIndex)
    {
        this.outputIndex = outputIndex;
    }

    public String getDestination()
    {
        return destination;
    }

    public void setDestination(String destination)
    {
        this.destination = destination;
    }
}
