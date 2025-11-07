package com.jeel.portfolio.web.controller;

import com.jeel.portfolio.domain.project.Project;
import com.jeel.portfolio.service.ProjectNotFoundException;
import com.jeel.portfolio.service.ProjectService;
import com.jeel.portfolio.web.view.AnalyticsSnippetBuilder;
import com.jeel.portfolio.web.view.PageMeta;
import com.jeel.portfolio.web.view.SeoMetadataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(com.jeel.portfolio.config.SecurityConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private SeoMetadataFactory seoMetadataFactory;

    @MockBean
    private AnalyticsSnippetBuilder analyticsSnippetBuilder;

    private Project sampleProject() {
        return new Project("Spring Boot Portfolio", "Spring & Thymeleaf", "spring-boot-portfolio",
                "Description", "spring,java", null, true);
    }

    @Test
    void listProjectsReturnsOk() throws Exception {
        when(projectService.search(anyString())).thenReturn(List.of(sampleProject()));
        when(projectService.allTags()).thenReturn(List.of("spring"));
        when(seoMetadataFactory.projectListing(Mockito.any())).thenReturn(PageMeta.of("t", "d", "/", null));

        mockMvc.perform(get("/project").param("q", "spring"))
                .andExpect(status().isOk());
    }

    @Test
    void projectDetailNotFoundReturns404() throws Exception {
        when(projectService.getBySlug("missing")).thenThrow(new ProjectNotFoundException("missing"));
        when(seoMetadataFactory.projectDetail(Mockito.any())).thenReturn(PageMeta.of("t", "d", "/", null));

        mockMvc.perform(get("/project/missing"))
                .andExpect(status().isNotFound());
    }
}
