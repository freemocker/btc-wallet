package io.acrosafe.wallet.btc.domain;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "wallet_record")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class WalletRecord implements Serializable
{
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "owner_seed", nullable = false)
    private String ownerSeed;

    @Column(name = "married_seed", nullable = false)
    private String marriedSeed;

    @Column(name = "signer_seed", nullable = false)
    private String signerSeed;

    @Column(name = "backup_signer_seed", nullable = false)
    private String backupSignerSeed;

    @Column(name = "signer_watching_key", nullable = false)
    private String signerWatchingKey;

    @Column(name = "backup_signer_watching_key", nullable = false)
    private String backupSignerWatchingKey;

    @Column(name = "owner_spec", nullable = false)
    private String ownerSpec;

    @Column(name = "owner_salt", nullable = false)
    private String ownerSalt;

    @Column(name = "signer_spec", nullable = false)
    private String signerSpec;

    @Column(name = "signer_salt", nullable = false)
    private String signerSalt;

    @Column(name = "label", nullable = true)
    private String label;

    @Column(name = "timestamp", nullable = false)
    private Long seedTimestamp;

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

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getOwnerSeed()
    {
        return ownerSeed;
    }

    public void setOwnerSeed(String ownerSeed)
    {
        this.ownerSeed = ownerSeed;
    }

    public String getMarriedSeed()
    {
        return marriedSeed;
    }

    public void setMarriedSeed(String marriedSeed)
    {
        this.marriedSeed = marriedSeed;
    }

    public String getSignerSeed()
    {
        return signerSeed;
    }

    public void setSignerSeed(String signerSeed)
    {
        this.signerSeed = signerSeed;
    }

    public String getBackupSignerSeed()
    {
        return backupSignerSeed;
    }

    public void setBackupSignerSeed(String backupSignerSeed)
    {
        this.backupSignerSeed = backupSignerSeed;
    }

    public String getSignerWatchingKey()
    {
        return signerWatchingKey;
    }

    public void setSignerWatchingKey(String signerWatchingKey)
    {
        this.signerWatchingKey = signerWatchingKey;
    }

    public String getBackupSignerWatchingKey()
    {
        return backupSignerWatchingKey;
    }

    public void setBackupSignerWatchingKey(String backupSignerWatchingKey)
    {
        this.backupSignerWatchingKey = backupSignerWatchingKey;
    }

    public String getOwnerSpec()
    {
        return ownerSpec;
    }

    public void setOwnerSpec(String ownerSpec)
    {
        this.ownerSpec = ownerSpec;
    }

    public String getOwnerSalt()
    {
        return ownerSalt;
    }

    public void setOwnerSalt(String ownerSalt)
    {
        this.ownerSalt = ownerSalt;
    }

    public String getSignerSpec()
    {
        return signerSpec;
    }

    public void setSignerSpec(String signerSpec)
    {
        this.signerSpec = signerSpec;
    }

    public String getSignerSalt()
    {
        return signerSalt;
    }

    public void setSignerSalt(String signerSalt)
    {
        this.signerSalt = signerSalt;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public Long getSeedTimestamp()
    {
        return seedTimestamp;
    }

    public void setSeedTimestamp(Long seedTimestamp)
    {
        this.seedTimestamp = seedTimestamp;
    }

    public Instant getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate)
    {
        this.createdDate = createdDate;
    }
}
