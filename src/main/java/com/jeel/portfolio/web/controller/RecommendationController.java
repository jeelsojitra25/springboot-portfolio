package com.jeel.portfolio.web.controller;

import com.jeel.portfolio.service.ProjectQaService;
import com.jeel.portfolio.service.ProjectRecommender;
import com.jeel.portfolio.service.QaResponse;
import com.jeel.portfolio.service.Recommendation;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final ProjectRecommender projectRecommender;
    private final ProjectQaService projectQaService;

    public RecommendationController(ProjectRecommender projectRecommender, ProjectQaService projectQaService) {
        this.projectRecommender = projectRecommender;
        this.projectQaService = projectQaService;
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> recommend(@RequestParam(name = "tags", required = false) String tagsParam,
                                       @RequestParam(name = "q", required = false) String question) {
        Recommendation recommendation;
        if (StringUtils.hasText(tagsParam)) {
            recommendation = projectRecommender.recommendByKeywords(splitTags(tagsParam));
        } else if (StringUtils.hasText(question)) {
            recommendation = projectRecommender.recommendByText(question);
        } else {
            recommendation = projectRecommender.recommendByKeywords(List.of());
        }

        if (recommendation == null || recommendation.project() == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(ProjectRecommendationResponse.from(recommendation));
    }

    @GetMapping("/recommend/qa")
    public ResponseEntity<QaResponse> qa(@RequestParam(name = "slug", required = false) String slug,
                                         @RequestParam(name = "question", required = false) String question) {
        QaResponse response = projectQaService.answer(slug, question);
        return ResponseEntity.ok(response);
    }

    private List<String> splitTags(String input) {
        return Arrays.stream(input.split(","))
                .map(tag -> tag.toLowerCase(Locale.ENGLISH).trim())
                .filter(StringUtils::hasText)
                .toList();
    }

    record ProjectRecommendationResponse(String title,
                                         String subtitle,
                                         String slug,
                                         List<String> tags,
                                         List<String> matchedKeywords,
                                         String reason) {
        static ProjectRecommendationResponse from(Recommendation recommendation) {
            var project = recommendation.project();
            String reason = recommendation.matchedKeywords().isEmpty()
                    ? "Top match based on recency and popularity."
                    : "Matches: " + String.join(", ", recommendation.matchedKeywords());
            return new ProjectRecommendationResponse(
                    project.getTitle(),
                    project.getSubtitle(),
                    project.getSlug(),
                    project.getTags(),
                    recommendation.matchedKeywords(),
                    reason
            );
        }
    }
}
