package com.jeel.portfolio.service;

import com.jeel.portfolio.domain.project.Project;
import com.jeel.portfolio.domain.project.ProjectRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<Project> search(String query) {
        if (!StringUtils.hasText(query)) {
            return findAll();
        }
        return projectRepository.search(query);
    }

    public Project getBySlug(String slug) {
        return projectRepository.findBySlug(slug)
                .orElseThrow(() -> new ProjectNotFoundException(slug));
    }

    public List<String> allTags() {
        return projectRepository.findAll().stream()
                .flatMap(project -> project.getTags().stream())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }
}
