package io.acrosafe.wallet.btc.web.rest.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.acrosafe.wallet.btc.util.Passphrase;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateWalletRequest
{
    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("label")
    private String label;

    @JsonProperty("signing_key_passphrase")
    private Passphrase signingKeyPassphrase;

    @JsonProperty("backup_signing_key_passphrase")
    private Passphrase backupSigningKeyPassphrase;

    @JsonProperty("enabled")
    private Boolean enabled;

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
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
