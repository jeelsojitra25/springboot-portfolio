package com.jeel.portfolio.web.view;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalViewAttributes {

    private final AnalyticsSnippetBuilder analyticsSnippetBuilder;

    public GlobalViewAttributes(AnalyticsSnippetBuilder analyticsSnippetBuilder) {
        this.analyticsSnippetBuilder = analyticsSnippetBuilder;
    }

    @ModelAttribute("analyticsSnippet")
    public String analyticsSnippet() {
        return analyticsSnippetBuilder.snippet().orElse(null);
    }
}
