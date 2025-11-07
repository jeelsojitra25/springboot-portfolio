package com.jeel.portfolio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "site")
public record SiteProperties(String baseUrl) {
}
