package com.jeel.portfolio.web.controller;

import com.jeel.portfolio.domain.project.ProjectRepository;
import com.jeel.portfolio.web.view.SeoMetadataFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProjectRepository projectRepository;
    private final SeoMetadataFactory seoMetadataFactory;

    public HomeController(ProjectRepository projectRepository, SeoMetadataFactory seoMetadataFactory) {
        this.projectRepository = projectRepository;
        this.seoMetadataFactory = seoMetadataFactory;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredProjects", projectRepository.findByFeaturedTrueOrderByCreatedAtDesc());
        model.addAttribute("meta", seoMetadataFactory.home());
        return "index";
    }
}
