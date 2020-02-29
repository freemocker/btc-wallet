package io.acrosafe.wallet.btc.web.rest.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateWalletResponse extends Response
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("created_date")
    private Instant createdDate;

    @JsonProperty("label")
    private String label;

    @JsonProperty("encrypted_signing_key")
    private String encryptedSigningKey;

    @JsonProperty("encrypted_backup_key")
    private String encryptedBackupKey;

    @JsonProperty("iv_spec")
    private String spec;

    @JsonProperty("salt")
    private String salt;

    @JsonProperty("seed_creation_time")
    private Long creationTime;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Boolean isEnabled()
    {
        return enabled;
    }

    public Instant getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate)
    {
        this.createdDate = createdDate;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getEncryptedSigningKey()
    {
        return encryptedSigningKey;
    }

    public void setEncryptedSigningKey(String encryptedSigningKey)
    {
        this.encryptedSigningKey = encryptedSigningKey;
    }

    public String getEncryptedBackupKey()
    {
        return encryptedBackupKey;
    }

    public void setEncryptedBackupKey(String encryptedBackupKey)
    {
        this.encryptedBackupKey = encryptedBackupKey;
    }

    public String getSpec()
    {
        return spec;
    }

    public void setSpec(String spec)
    {
        this.spec = spec;
    }

    public String getSalt()
    {
        return salt;
    }

    public void setSalt(String salt)
    {
        this.salt = salt;
    }

    public Long getCreationTime()
    {
        return creationTime;
    }

    public void setCreationTime(Long creationTime)
    {
        this.creationTime = creationTime;
    }

}
