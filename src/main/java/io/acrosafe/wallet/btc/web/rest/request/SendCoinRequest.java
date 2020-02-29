package io.acrosafe.wallet.btc.web.rest.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.acrosafe.wallet.btc.util.Passphrase;

public class SendCoinRequest
{
    @JsonProperty("recipients")
    private List<Recipient> recipients;

    @JsonProperty("internal_id")
    private String internalId;

    @JsonProperty("number_of_block")
    private Integer numberOfBlock;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("signing_key_passphrase")
    private Passphrase signingKeyPassphrase;

    @JsonProperty("backup_signing_key_passphrase")
    private Passphrase backupSigningKeyPassphrase;

    @JsonProperty("using_backup_signer")
    private Boolean usingBackupSigner;

    @JsonProperty("memo")
    private String memo;

    public List<Recipient> getRecipients()
    {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients)
    {
        this.recipients = recipients;
    }

    public void addRecipient(Recipient recipient)
    {
        if (recipients == null)
        {
            recipients = new ArrayList<>();
        }

        recipients.add(recipient);
    }

    public String getInternalId()
    {
        return internalId;
    }

    public void setInternalId(String internalId)
    {
        this.internalId = internalId;
    }

    public Integer getNumberOfBlock()
    {
        return numberOfBlock;
    }

    public void setNumberOfBlock(Integer numberOfBlock)
    {
        this.numberOfBlock = numberOfBlock;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public Boolean getUsingBackupSigner()
    {
        return usingBackupSigner;
    }

    public void setUsingBackupSigner(Boolean usingBackupSigner)
    {
        this.usingBackupSigner = usingBackupSigner;
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo(String memo)
    {
        this.memo = memo;
    }

    public Passphrase getSigningKeyPassphrase()
    {
        return signingKeyPassphrase;
    }

    public void setSigningKeyPassphrase(String signingKeyPassphrase)
    {
        this.signingKeyPassphrase = new Passphrase(signingKeyPassphrase);
    }

    public Passphrase getBackupSigningKeyPassphrase()
    {
        return backupSigningKeyPassphrase;
    }

    public void setBackupSigningKeyPassphrase(String backupSigningKeyPassphrase)
    {
        this.backupSigningKeyPassphrase = new Passphrase(backupSigningKeyPassphrase);
    }
}
