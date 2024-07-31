package com.atoss.idea.management.system.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuration class for settings up CORS settings.
 */
@Component
public class CorsConfig {

    @Value("${cors.allowedOrigins}")
    private String allowedOrigin;

    @Value("${cors.allowedMethods}")
    private String allowedMethod;

    @Value("${cors.allowedHeaders}")
    private String allowedHeaders;

    /**
     * Creates a CORS (Cross-Origin Resource Sharing) filter bean that allows origins, headers and methods
     * @return Return a configured with the specified CORS settings
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(allowedOrigin);
        config.addAllowedHeader(allowedHeaders);
        config.addAllowedMethod(allowedMethod);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
