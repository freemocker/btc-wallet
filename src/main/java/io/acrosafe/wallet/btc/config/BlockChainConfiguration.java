package io.acrosafe.wallet.btc.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.acrosafe.wallet.core.btc.SeedGenerator;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.acrosafe.wallet.core.btc.BlockChainNetwork;
import io.acrosafe.wallet.core.btc.Network;

@Configuration
public class BlockChainConfiguration
{
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(BlockChainConfiguration.class);

    private final Environment env;
    private NetworkParameters networkParameters;

    public BlockChainConfiguration(Environment env)
    {
        this.env = env;

        final String network = env.getProperty("application.network");
        logger.info("initialized blockchain network. network = {}", network);
        networkParameters = initializeNetworkProperties(network);
    }

    @Bean
    public NetworkParameters networkParameters()
    {
        return networkParameters;
    }

    @Bean
    public BlockChainNetwork blockChainNetwork()
    {
        final long fastCatchupTime = getFastCatchupTime(env.getProperty("application.fast-catchup-time"));
        final String dnsSeeds = env.getProperty("application.dns-seeds");
        final String serviceId = env.getProperty("application.service-id");

        logger.info("creating blockchain network instance. fastCatchuptime = {}, dnsSeeds = {}, serviceId = {}", fastCatchupTime,
                dnsSeeds, serviceId);
        return new BlockChainNetwork(fastCatchupTime, dnsSeeds.split(","), networkParameters, serviceId);
    }

    @Bean
    public SeedGenerator seedGenerator()
    {
        return new SeedGenerator();
    }

    public long getFastCatchupTime(String fastCatchupTime)
    {
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = (Date) dateFormat.parse(fastCatchupTime);
            return (date.getTime()) / 1000;
        }
        catch (Throwable t)
        {
            return 0L;
        }
    }

    /**
     * Initializes BTC network parameters.
     *
     * @return
     */
    private NetworkParameters initializeNetworkProperties(String network)
    {
        if (StringUtils.isEmpty(network))
        {
            logger.warn("BTC network is not defined. Testnet will be used by default.");
            return TestNet3Params.get();
        }

        if (network.equalsIgnoreCase(Network.REGTEST.getNetwork()))
        {
            return RegTestParams.get();
        }
        else if (network.equalsIgnoreCase(Network.MAINNET.getNetwork()))
        {
            return MainNetParams.get();
        }
        else
        {
            return TestNet3Params.get();
        }
    }
}
