package com.jeel.portfolio.service;

import com.jeel.portfolio.domain.project.Project;
import com.jeel.portfolio.domain.project.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ProjectRecommenderTest {

    private ProjectRepository projectRepository;
    private ProjectRecommender recommender;

    @BeforeEach
    void setUp() {
        projectRepository = Mockito.mock(ProjectRepository.class);
        recommender = new ProjectRecommender(projectRepository);
    }

    @Test
    void prefersProjectsMatchingKeywords() {
        var springProject = new Project("Spring Boot Portfolio", "Spring & Thymeleaf", "spring",
                "Built with Spring Boot and Thymeleaf templates.", "spring,java,web", null, true);
        var unityProject = new Project("Unity FPS", "Game AI", "unity",
                "Unity FPS experiment.", "unity,ai", null, false);
        when(projectRepository.findAll()).thenReturn(List.of(unityProject, springProject));

        Recommendation result = recommender.recommendByKeywords(List.of("spring"));

        assertThat(result.project()).isEqualTo(springProject);
        assertThat(result.matchedKeywords()).contains("spring");
    }

    @Test
    void fallsBackToFeaturedWhenNoKeywords() {
        var featured = new Project("Featured", "Feat", "featured",
                "Desc", "java", null, true);
        when(projectRepository.findAll()).thenReturn(List.of(featured));

        Recommendation result = recommender.recommendByKeywords(List.of());

        assertThat(result.project()).isEqualTo(featured);
        assertThat(result.matchedKeywords()).contains("featured");
    }
}
