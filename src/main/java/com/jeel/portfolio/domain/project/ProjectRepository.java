package com.jeel.portfolio.domain.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findBySlug(String slug);

    @Query("""
            select p from Project p
            where lower(p.title) like lower(concat('%', :q, '%'))
               or lower(p.subtitle) like lower(concat('%', :q, '%'))
               or lower(p.description) like lower(concat('%', :q, '%'))
               or lower(p.tagsCsv) like lower(concat('%', :q, '%'))
            order by p.createdAt desc
            """)
    List<Project> search(@Param("q") String query);

    List<Project> findByFeaturedTrueOrderByCreatedAtDesc();
}
