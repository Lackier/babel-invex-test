package com.babel.employeeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Set;

/**
 * Logs method, URI, query string and headers for every request at DEBUG level
 * (see logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter).
 * Authorization/Cookie headers are excluded even though there is no auth today,
 * so this stays safe if security is added later.
 */
@Configuration
public class RequestLoggingConfig {

    private static final Set<String> EXCLUDED_HEADERS = Set.of("authorization", "cookie");

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(false);
        filter.setHeaderPredicate(header -> !EXCLUDED_HEADERS.contains(header.toLowerCase()));
        return filter;
    }
}
