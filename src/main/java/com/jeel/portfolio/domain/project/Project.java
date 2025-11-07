package com.jeel.portfolio.domain.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 255)
    private String subtitle;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "tags", length = 255)
    private String tagsCsv;

    @Column(name = "hero_image")
    private String heroImage;

    private boolean featured;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Project() {
        // for JPA
    }

    public Project(String title,
                   String subtitle,
                   String slug,
                   String description,
                   String tagsCsv,
                   String heroImage,
                   boolean featured) {
        this.title = title;
        this.subtitle = subtitle;
        this.slug = slug;
        this.description = description;
        this.tagsCsv = tagsCsv;
        this.heroImage = heroImage;
        this.featured = featured;
    }

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getTagsCsv() {
        return tagsCsv;
    }

    public String getHeroImage() {
        return heroImage;
    }

    public boolean isFeatured() {
        return featured;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<String> getTags() {
        if (tagsCsv == null || tagsCsv.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tagsCsv.split(","))
                .map(String::trim)
                .map(tag -> tag.toLowerCase(Locale.ENGLISH))
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
    }
}
