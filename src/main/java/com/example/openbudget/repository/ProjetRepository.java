package com.example.openbudget.repository;

import com.example.openbudget.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetRepository extends JpaRepository<Project, Integer> {
    boolean existsByTitleId(Integer id);

    Optional<Project> findByTitleId(Integer id);

        List<Project> findAllByStatusTrue();

    /**
     * delete from projects where status is false
     */
    void deleteAllByStatusFalse();
}
