package com.jeel.portfolio.service;

import com.jeel.portfolio.domain.project.Project;

import java.util.List;

public record Recommendation(Project project, double score, List<String> matchedKeywords) {
}
