package io.acrosafe.wallet.btc.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;

public final class DefaultProfileUtil
{

    private static final String SPRING_PROFILE_DEFAULT = "spring.profiles.default";
    private static final String SPRING_PROFILE_DEVELOPMENT = "dev";

    private DefaultProfileUtil()
    {
    }

    /**
     * Set a default to use when no profile is configured.
     *
     * @param app
     *            the Spring application.
     */
    public static void addDefaultProfile(SpringApplication app)
    {
        Map<String, Object> defProperties = new HashMap<>();
        defProperties.put(SPRING_PROFILE_DEFAULT, SPRING_PROFILE_DEVELOPMENT);
        app.setDefaultProperties(defProperties);
    }
}
