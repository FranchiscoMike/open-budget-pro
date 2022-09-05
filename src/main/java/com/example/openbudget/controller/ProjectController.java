package com.example.openbudget.controller;

import com.example.openbudget.dto.ApiResponse;
import com.example.openbudget.dto.ProjectDTO;
import com.example.openbudget.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.DELETE;

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

    @DeleteMapping("/{id}")
    private HttpEntity<?> delete(@PathVariable("id")Integer id) {
        ApiResponse delete = projectService.delete(id);
        return ResponseEntity.status(delete.isSuccess() ? 201 : 409).body(delete);
    }

    @GetMapping("/all_projects")
    private HttpEntity<?> all_1() {
        ApiResponse all = projectService.all_1();
        return ResponseEntity.status(all.isSuccess() ? 201 : 409).body(all);
    }



}
