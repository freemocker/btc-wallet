/**
 * MIT License
 *
 * Copyright (c) 2020 acrosafe technologies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
