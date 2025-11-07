package com.jeel.portfolio.service;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String slug) {
        super("Project not found for slug: " + slug);
    }
}
