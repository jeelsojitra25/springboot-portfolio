package com.jeel.portfolio.web.view;

import java.util.Objects;

public record PageMeta(
        String title,
        String description,
        String canonicalUrl,
        String imageUrl
) {
    public PageMeta {
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(canonicalUrl, "canonicalUrl must not be null");
    }

    public static PageMeta of(String title, String description, String canonicalUrl, String imageUrl) {
        return new PageMeta(title, description, canonicalUrl, imageUrl);
    }
}
