package com.jeel.portfolio.service;

import com.jeel.portfolio.domain.project.Project;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ProjectQaService {

    private final ProjectService projectService;
    private final ProjectRecommender projectRecommender;

    public ProjectQaService(ProjectService projectService, ProjectRecommender projectRecommender) {
        this.projectService = projectService;
        this.projectRecommender = projectRecommender;
    }

    public QaResponse answer(String slug, String question) {
        Project project = resolveProject(slug, question);
        if (project == null) {
            return new QaResponse(null, null, "No projects available yet. Check back soon!", "no-data");
        }

        List<String> tokens = tokenize(question);
        List<String> overlaps = project.getTags().stream()
                .filter(tag -> tokens.contains(tag))
                .collect(Collectors.toList());

        String snippet = extractSnippet(project, overlaps);
        String reason = overlaps.isEmpty()
                ? "Based on closest overall match."
                : "Matches interests: " + String.join(", ", overlaps);

        String answer = """
                %s leans on %s. %s
                Explore more: /project/%s
                """.formatted(
                project.getTitle(),
                overlaps.isEmpty() ? project.getSubtitle() : String.join(" & ", overlaps),
                snippet,
                project.getSlug());

        return new QaResponse(project.getTitle(), project.getSlug(), answer.trim(), reason);
    }

    private Project resolveProject(String slug, String question) {
        if (StringUtils.hasText(slug)) {
            try {
                return projectService.getBySlug(slug);
            } catch (RuntimeException ignored) {
                // fallback
            }
        }
        if (StringUtils.hasText(question)) {
            Recommendation rec = projectRecommender.recommendByText(question);
            if (rec != null) {
                return rec.project();
            }
        }
        List<Project> projects = projectService.findAll();
        return projects.isEmpty() ? null : projects.get(0);
    }

    private String extractSnippet(Project project, List<String> overlaps) {
        String description = project.getDescription();
        if (!StringUtils.hasText(description)) {
            return "It's a concise showcase piece.";
        }

        BreakIterator sentences = BreakIterator.getSentenceInstance(Locale.ENGLISH);
        sentences.setText(description);
        int start = sentences.first();
        for (int end = sentences.next(); end != BreakIterator.DONE; start = end, end = sentences.next()) {
            String sentence = description.substring(start, end).trim();
            if (overlaps.stream().anyMatch(sentence.toLowerCase(Locale.ENGLISH)::contains)) {
                return sentence;
            }
        }
        return description.length() > 140 ? description.substring(0, 140) + "…" : description;
    }

    private List<String> tokenize(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        return List.of(text.toLowerCase(Locale.ENGLISH).split("[^a-z0-9]+"))
                .stream()
                .filter(StringUtils::hasText)
                .toList();
    }
}
