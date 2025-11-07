package com.jeel.portfolio.web.view;

import com.jeel.portfolio.config.SiteProperties;
import com.jeel.portfolio.domain.project.Project;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SeoMetadataFactory {

    private final SiteProperties siteProperties;

    public SeoMetadataFactory(SiteProperties siteProperties) {
        this.siteProperties = siteProperties;
    }

    public PageMeta home() {
        return PageMeta.of(
                "Jeel Sojitra | Portfolio",
                "Software developer crafting Spring Boot, Unity, and data projects. Explore work, projects, and ways to collaborate.",
                siteProperties.baseUrl() + "/",
                siteProperties.baseUrl() + "/images/social/home.svg"
        );
    }

    public PageMeta about() {
        return PageMeta.of(
                "About | Jeel Sojitra",
                "Learn about Jeel's background, favorite technologies, and approach to building impactful software products.",
                siteProperties.baseUrl() + "/about",
                siteProperties.baseUrl() + "/images/social/about.svg"
        );
    }

    public PageMeta contact() {
        return PageMeta.of(
                "Contact | Jeel Sojitra",
                "Reach out to collaborate on Spring, Unity, or data-rich projects.",
                siteProperties.baseUrl() + "/contact",
                siteProperties.baseUrl() + "/images/social/contact.svg"
        );
    }

    public PageMeta projectListing(String query) {
        String suffix = (StringUtils.hasText(query) ? " – Searching \"" + query + "\"" : "");
        return PageMeta.of(
                "Projects" + suffix + " | Jeel Sojitra",
                "Curated project collection across Spring Boot, Unity, SQL, and AI experiments.",
                siteProperties.baseUrl() + "/project",
                siteProperties.baseUrl() + "/images/social/projects.svg"
        );
    }

    public PageMeta projectDetail(Project project) {
        String description = project.getSubtitle() != null ? project.getSubtitle() : project.getDescription();
        return PageMeta.of(
                project.getTitle() + " | Jeel Sojitra",
                description,
                siteProperties.baseUrl() + "/project/" + project.getSlug(),
                siteProperties.baseUrl() + (project.getHeroImage() != null ? project.getHeroImage() : "/images/social/projects.svg")
        );
    }
}
