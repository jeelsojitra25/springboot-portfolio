package com.jeel.portfolio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "analytics")
public record AnalyticsProperties(
        boolean enabled,
        String provider,
        String scriptHost,
        String siteId
) {
}
