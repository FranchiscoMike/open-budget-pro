package com.example.openbudget.controller;

import com.example.openbudget.dto.AdminDTO;
import com.example.openbudget.dto.ApiResponse;
import com.example.openbudget.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.POST;

@RestController
@CrossOrigin()
@RequestMapping("/v1/admins")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService service;

    /**
     * all admins
     *
     * @return
     */
    @GetMapping()
    private HttpEntity<?> all() {
        ApiResponse all = service.all();
        return ResponseEntity.status(all.isSuccess() ? 201 : 409).body(all);
    }

    /**
     * get one user via id
     *
     * @return
     */
    @GetMapping("/{id}")
    private HttpEntity<?> all(@PathVariable("id") Integer id) {
        ApiResponse all = service.one(id);
        return ResponseEntity.status(all.isSuccess() ? 201 : 409).body(all);
    }

    @PostMapping("/login")
    private HttpEntity<?> login(
            @RequestBody AdminDTO dto) {
        ApiResponse login = service.login(dto);
        return ResponseEntity.status(login.isSuccess() ? 201 : 409).body(login);
    }

    @PostMapping()
    private HttpEntity<?> create(@RequestBody AdminDTO dto) {
        ApiResponse create = service.create(dto);
        return ResponseEntity.status(create.isSuccess() ? 201 : 409).body(create);
    }

    @PatchMapping("/{id}")
    private HttpEntity<?> update(@PathVariable("id") Integer id,@RequestBody AdminDTO dto) throws Exception {
        ApiResponse update = service.update(id,dto);
        return ResponseEntity.status(update.isSuccess() ? 201 : 409).body(update);
    }

    @DeleteMapping("/{id}")
    private HttpEntity<?> delete(@PathVariable("id") Integer id){
        ApiResponse delete = service.delete(id);
        return ResponseEntity.status(delete.isSuccess() ? 201 : 404).body(delete);
    }

}
