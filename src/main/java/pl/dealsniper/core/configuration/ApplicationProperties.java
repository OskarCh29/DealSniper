/* (C) 2025 */
package pl.dealsniper.core.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private String baseUrl;
    private AppConfig config;


    @Getter
    @Setter
    public static class AppConfig{
        private int taskPerUser;
        private int threadMaxPoolSize;
        private int schedulerHourInterval;
    }
}
