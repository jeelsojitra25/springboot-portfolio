package com.jeel.portfolio.web.controller;

import com.jeel.portfolio.service.ProjectService;
import com.jeel.portfolio.web.view.SeoMetadataFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProjectController {

    private final ProjectService projectService;
    private final SeoMetadataFactory seoMetadataFactory;

    public ProjectController(ProjectService projectService, SeoMetadataFactory seoMetadataFactory) {
        this.projectService = projectService;
        this.seoMetadataFactory = seoMetadataFactory;
    }

    @GetMapping("/project")
    public String list(@RequestParam(name = "q", required = false) String query, Model model) {
        model.addAttribute("projects", projectService.search(query));
        model.addAttribute("tags", projectService.allTags());
        model.addAttribute("query", query);
        model.addAttribute("meta", seoMetadataFactory.projectListing(query));
        return "project-list";
    }

    @GetMapping("/project/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        try {
            var project = projectService.getBySlug(slug);
            model.addAttribute("project", project);
            model.addAttribute("meta", seoMetadataFactory.projectDetail(project));
            return "project-detail";
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found", ex);
        }
    }
}
