package com.example.openbudget.service;

import com.example.openbudget.dto.ApiResponse;
import com.example.openbudget.dto.ProjectDTO;
import com.example.openbudget.entity.Project;
import com.example.openbudget.repository.ProjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjetRepository repository;

    public ApiResponse create(ProjectDTO dto) {
        boolean optional = repository.existsByTitleId(dto.getTitle_id());
        if (!optional) {
            Project project = new Project();

            project.setTitle(dto.getTitle());
            project.setTitleId(dto.getTitle_id());
            project.setStatus(dto.isStatus());

            Project save = repository.save(project);

            return new ApiResponse(true, "done", save);
        }
        return new ApiResponse(false, "already exists");
    }

    public ApiResponse all() {
        List<Project> all = repository.findAllByStatusTrue();
        return new ApiResponse(true, "All projects", all);
    }

    public ApiResponse delete(Integer id) {
        Optional<Project> byId = repository.findById(id);

        if (byId.isPresent()) {
            Project project = byId.get();
            project.setStatus(false);
            repository.save(project); //save project
            return new ApiResponse(true,"Deleted project successfully");
        }
        return new ApiResponse(false,"Project not found");
    }
}
