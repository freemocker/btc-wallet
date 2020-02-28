package io.acrosafe.wallet.btc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import io.acrosafe.wallet.btc.config.ApplicationProperties;
import io.acrosafe.wallet.btc.config.DefaultProfileUtil;

@SpringBootApplication
@EnableConfigurationProperties({ ApplicationProperties.class })
public class BtcWalletApplication
{
    private static final Logger logger = LoggerFactory.getLogger(BtcWalletApplication.class);

    public static void main(String[] args)
    {
        SpringApplication app = new SpringApplication(BtcWalletApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env)
    {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null)
        {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath))
        {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try
        {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
            logger.warn("The host name could not be determined, using `localhost` as fallback");
        }
        logger.info("\n----------------------------------------------------------\n\t"
                + "Application '{}' is running! Access URLs:\n\t" + "Local: \t\t{}://localhost:{}{}\n\t"
                + "External: \t{}://{}:{}{}\n\t" + "Profile(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"), protocol, serverPort, contextPath, protocol, hostAddress, serverPort,
                contextPath, env.getActiveProfiles());
    }

}
