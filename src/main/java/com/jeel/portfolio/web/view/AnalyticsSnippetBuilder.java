package com.jeel.portfolio.web.view;

import com.jeel.portfolio.config.AnalyticsProperties;
import com.jeel.portfolio.config.SiteProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;

@Component
public class AnalyticsSnippetBuilder {

    private final AnalyticsProperties analyticsProperties;
    private final SiteProperties siteProperties;

    public AnalyticsSnippetBuilder(AnalyticsProperties analyticsProperties, SiteProperties siteProperties) {
        this.analyticsProperties = analyticsProperties;
        this.siteProperties = siteProperties;
    }

    public Optional<String> snippet() {
        if (!analyticsProperties.enabled()) {
            return Optional.empty();
        }

        String provider = Optional.ofNullable(analyticsProperties.provider())
                .map(value -> value.toLowerCase(Locale.ENGLISH))
                .orElse("none");

        return switch (provider) {
            case "plausible" -> Optional.of(plausibleSnippet());
            case "umami" -> Optional.ofNullable(umamiSnippet());
            default -> Optional.empty();
        };
    }

    private String plausibleSnippet() {
        String domain = resolveDomainForAnalytics();
        String host = StringUtils.hasText(analyticsProperties.scriptHost())
                ? analyticsProperties.scriptHost()
                : "https://plausible.io";
        return """
                <script defer data-domain="%s" src="%s/js/script.js"></script>
                """.formatted(domain, host);
    }

    private String umamiSnippet() {
        if (!StringUtils.hasText(analyticsProperties.siteId())
                || !StringUtils.hasText(analyticsProperties.scriptHost())) {
            return null;
        }
        return """
                <script async src="%s/script.js" data-website-id="%s"></script>
                """.formatted(analyticsProperties.scriptHost(), analyticsProperties.siteId());
    }

    private String resolveDomainForAnalytics() {
        try {
            var uri = URI.create(siteProperties.baseUrl());
            return uri.getHost() != null ? uri.getHost() : siteProperties.baseUrl();
        } catch (Exception ex) {
            return siteProperties.baseUrl();
        }
    }
}
