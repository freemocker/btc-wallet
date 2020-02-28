package io.acrosafe.wallet.btc.config;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;

@Configuration
public class WebConfiguration implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory>
{
    private final Logger logger = LoggerFactory.getLogger(WebConfiguration.class);

    private final Environment env;

    public WebConfiguration(Environment env)
    {
        this.env = env;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException
    {
        if (env.getActiveProfiles().length != 0)
        {
            logger.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
        }
        EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        logger.info("Web application fully configured {}", disps);
    }

    @Override
    public void customize(WebServerFactory server)
    {
        if (server instanceof ConfigurableServletWebServerFactory)
        {
            MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
            mappings.add("html", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
            mappings.add("json", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
            ConfigurableServletWebServerFactory servletWebServer = (ConfigurableServletWebServerFactory) server;
            servletWebServer.setMimeMappings(mappings);
        }
    }

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory()
    {
        TomcatServletWebServerFactory tomcatContainer = new TomcatServletWebServerFactory();
        return tomcatContainer;
    }
}
