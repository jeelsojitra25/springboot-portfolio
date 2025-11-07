package com.jeel.portfolio.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

public class SecurityHeadersFilter extends OncePerRequestFilter {

    private final AnalyticsProperties analyticsProperties;

    public SecurityHeadersFilter(AnalyticsProperties analyticsProperties) {
        this.analyticsProperties = analyticsProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Permissions-Policy", "geolocation=()");
        response.setHeader("X-XSS-Protection", "0");
        response.setHeader("Content-Security-Policy", buildCsp());

        filterChain.doFilter(request, response);
    }

    private String buildCsp() {
        String analytics = analyticsProperties.enabled() && analyticsProperties.scriptHost() != null
                ? analyticsProperties.scriptHost()
                : "";
        String normalizedAnalytics = analytics.isBlank() ? "" : " " + analytics;
        return ("default-src 'self'" + normalizedAnalytics + "; " +
                "img-src 'self' data:; " +
                "script-src 'self'" + normalizedAnalytics + "; " +
                "style-src 'self' 'unsafe-inline'; " +
                "font-src 'self'; " +
                "connect-src 'self'" + normalizedAnalytics + "; " +
                "frame-ancestors 'none';").trim();
    }
}
