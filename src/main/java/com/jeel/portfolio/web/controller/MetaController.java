package com.jeel.portfolio.web.controller;

import com.jeel.portfolio.config.SiteProperties;
import com.jeel.portfolio.domain.project.ProjectRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@RestController
public class MetaController {

    private final SiteProperties siteProperties;
    private final ProjectRepository projectRepository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public MetaController(SiteProperties siteProperties, ProjectRepository projectRepository) {
        this.siteProperties = siteProperties;
        this.projectRepository = projectRepository;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        var base = siteProperties.baseUrl();
        var staticUrls = """
                <url><loc>%s/</loc></url>
                <url><loc>%s/about</loc></url>
                <url><loc>%s/project</loc></url>
                <url><loc>%s/contact</loc></url>
                """.formatted(base, base, base, base);

        var projectUrls = projectRepository.findAll().stream()
                .map(project -> """
                        <url>
                            <loc>%s/project/%s</loc>
                            <lastmod>%s</lastmod>
                        </url>
                        """.formatted(base, project.getSlug(), ISO_FORMATTER.format(project.getCreatedAt())))
                .collect(Collectors.joining());

        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                %s
                %s
                </urlset>
                """.formatted(staticUrls, projectUrls);
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String robots() {
        return """
                User-agent: *
                Allow: /

                Sitemap: %s/sitemap.xml
                """.formatted(siteProperties.baseUrl());
    }
}
