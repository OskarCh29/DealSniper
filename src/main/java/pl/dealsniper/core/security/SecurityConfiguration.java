/* (C) 2025 */
package pl.dealsniper.core.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "app.config.single-user.enabled", havingValue = "false", matchIfMissing = true)
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("APPLICATION IS RUNNING IN - SECURITY MODE");

        http.csrf(Customizer.withDefaults()).authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/sources/**")
                .authenticated()
                .requestMatchers("api/v1/scheduler/**")
                .authenticated()
                .requestMatchers("/api/v1/deals/**")
                .authenticated()
                .requestMatchers("/api/v1/users/**")
                .permitAll()
                .requestMatchers("/api-docs", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**"));
        return http.build();
    }
}
