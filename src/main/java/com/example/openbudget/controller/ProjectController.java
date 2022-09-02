package com.example.openbudget.controller;

import com.example.openbudget.dto.ApiResponse;
import com.example.openbudget.dto.ProjectDTO;
import com.example.openbudget.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin()
@RequiredArgsConstructor
@RequestMapping("/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping()
    private HttpEntity<?> create(@RequestBody() ProjectDTO dto) {
        ApiResponse create = projectService.create(dto);
        return ResponseEntity.status(create.isSuccess() ? 201 : 409).body(create);
    }

    @GetMapping()
    private HttpEntity<?> all() {
        ApiResponse all = projectService.all();
        return ResponseEntity.status(all.isSuccess() ? 201 : 409).body(all);
    }


}
