package com.jeel.portfolio.web.controller;

import com.jeel.portfolio.web.view.SeoMetadataFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticPageController {

    private final SeoMetadataFactory seoMetadataFactory;

    public StaticPageController(SeoMetadataFactory seoMetadataFactory) {
        this.seoMetadataFactory = seoMetadataFactory;
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("meta", seoMetadataFactory.about());
        return "about";
    }

}
