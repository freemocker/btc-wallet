package io.acrosafe.wallet.btc.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.acrosafe.wallet.btc.util.Passphrase;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties
{
    private String network;
    private long fastCatchupTime;
    private int depositConfirmationNumber = 2;
    private String serviceId;
    private Passphrase passphrase;
    private String[] dnsSeeds;
    private int entropyBits = 256;

    public String getNetwork()
    {
        return network;
    }

    public void setNetwork(String network)
    {
        this.network = network;
    }

    public int getEntropyBits()
    {
        return entropyBits;
    }

    public void setEntropyBits(int entropyBits)
    {
        this.entropyBits = entropyBits;
    }

    public long getFastCatchupTime()
    {
        return this.fastCatchupTime;
    }

    public void setFastCatchupTime(String fastCatchupTime)
    {
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = (Date) dateFormat.parse(fastCatchupTime);
            this.fastCatchupTime = (date.getTime()) / 1000;
        }
        catch (Throwable t)
        {
            this.fastCatchupTime = 0L;
        }
    }

    public int getDepositConfirmationNumber()
    {
        return depositConfirmationNumber;
    }

    public void setDepositConfirmationNumber(int depositConfirmationNumber)
    {
        this.depositConfirmationNumber = depositConfirmationNumber;
    }

    public Passphrase getPassphrase()
    {
        return passphrase;
    }

    public void setPassphrase(String passphrase)
    {
        this.passphrase = new Passphrase(passphrase);
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    public String[] getDNSSeeds()
    {
        return dnsSeeds;
    }

    public void setDNSSeeds(String[] dnsSeeds)
    {
        this.dnsSeeds = dnsSeeds;
    }
}
