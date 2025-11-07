package com.jeel.portfolio.service;

import com.jeel.portfolio.domain.project.Project;
import com.jeel.portfolio.domain.project.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class ProjectRecommender {

    private final ProjectRepository projectRepository;

    public ProjectRecommender(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Recommendation recommendByKeywords(List<String> keywords) {
        List<String> tokens = normalizeTokens(keywords);
        return bestMatch(tokens);
    }

    public Recommendation recommendByText(String text) {
        return bestMatch(tokenize(text));
    }

    private Recommendation bestMatch(List<String> tokens) {
        List<Project> projects = projectRepository.findAll();
        if (projects.isEmpty()) {
            return null;
        }

        if (tokens.isEmpty()) {
            Project featured = projects.stream()
                    .filter(Project::isFeatured)
                    .findFirst()
                    .orElse(projects.get(0));
            return new Recommendation(featured, featured.isFeatured() ? 2.0 : 1.0, List.of("featured"));
        }

        return projects.stream()
                .map(project -> score(project, tokens))
                .max(Comparator.comparingDouble(Recommendation::score))
                .orElseGet(() -> new Recommendation(projects.get(0), 0, List.of()));
    }

    private Recommendation score(Project project, List<String> tokens) {
        Map<String, Integer> frequencies = buildFrequencies(project);
        double score = 0;
        List<String> matched = new ArrayList<>();

        for (String token : tokens) {
            int weight = frequencies.getOrDefault(token, 0);
            if (weight > 0) {
                score += weight;
                matched.add(token);
            }
        }

        long sharedTags = project.getTags().stream()
                .filter(tokens::contains)
                .count();
        score += sharedTags * 0.75;

        if (project.isFeatured()) {
            score += 0.5;
        }

        return new Recommendation(project, score, matched);
    }

    private Map<String, Integer> buildFrequencies(Project project) {
        Map<String, Integer> freq = new HashMap<>();
        project.getTags().forEach(tag -> freq.merge(tag, 2, Integer::sum));
        incrementTokens(freq, tokenize(project.getTitle()), 2);
        incrementTokens(freq, tokenize(project.getSubtitle()), 1);
        incrementTokens(freq, tokenize(project.getDescription()), 1);
        return freq;
    }

    private void incrementTokens(Map<String, Integer> freq, List<String> tokens, int weight) {
        for (String token : tokens) {
            freq.merge(token, weight, Integer::sum);
        }
    }

    private List<String> normalizeTokens(List<String> keywords) {
        if (keywords == null) {
            return List.of();
        }
        return keywords.stream()
                .filter(StringUtils::hasText)
                .map(token -> token.toLowerCase(Locale.ENGLISH).trim())
                .filter(token -> token.length() > 1)
                .distinct()
                .toList();
    }

    private List<String> tokenize(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        Set<String> tokens = new HashSet<>();
        BreakIterator iterator = BreakIterator.getWordInstance(Locale.ENGLISH);
        iterator.setText(text.toLowerCase(Locale.ENGLISH));
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String word = text.substring(start, end).toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9]", "");
            if (word.length() > 1) {
                tokens.add(word);
            }
        }
        return tokens.stream().toList();
    }
}
