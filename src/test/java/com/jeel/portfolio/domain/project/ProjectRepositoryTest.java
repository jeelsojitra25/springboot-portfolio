package com.jeel.portfolio.domain.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void searchReturnsMatchesAcrossFields() {
        projectRepository.saveAll(List.of(
                new Project("Spring AI Assistant", "AI helper", "spring-ai-assistant",
                        "Spring service with AI components", "spring,ai", null, true),
                new Project("Plain Unity Game", "Unity fun", "unity-fun",
                        "A fun shooter built in Unity", "unity,csharp", null, false)
        ));

        var results = projectRepository.search("spring");

        assertThat(results)
                .extracting(Project::getSlug)
                .contains("spring-ai-assistant")
                .doesNotContain("unity-fun");
    }
}
