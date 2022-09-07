package com.example.openbudget.service;

import com.example.openbudget.dto.AdminDTO;
import com.example.openbudget.dto.ApiResponse;
import com.example.openbudget.entity.Admin;
import com.example.openbudget.repository.AdminRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminService {

    private final AdminRepository repository;

    public ApiResponse all() {
        return new ApiResponse(true, "All admins", repository.findAll());
    }

    public ApiResponse one(Integer id) {
        Optional<Admin> byId = repository.findById(id);

        return byId.map(admin -> new ApiResponse(false, "not found admin", admin)).orElseGet(() -> new ApiResponse(true, "found", byId.get()));
    }

    public ApiResponse login(AdminDTO dto) {
        Optional<Admin> byUsername = repository.findByUsername(dto.getUsername());

        if (byUsername.isPresent()) {
            Admin admin = byUsername.get();

            if (admin.getPassword().equals(dto.getPassword())) {
                return new ApiResponse(true, "Successfully signed", admin);
            } else {
                return new ApiResponse(false, "Some credentials don't match");
            }
        }
        return new ApiResponse(false, "User not found");
    }

    public ApiResponse create(AdminDTO dto) {
        Optional<Admin> byUsername = repository.findByUsername(dto.getUsername());

        if (byUsername.isPresent()) {
            return new ApiResponse(false, "Username should be unique!!!");
        }

        Admin admin = new Admin();

        admin.setUsername(dto.getUsername());
        admin.setPassword(dto.getPassword());

        Admin save = repository.save(admin);
        return new ApiResponse(true, "Successfully saved admin", save);
    }

    public ApiResponse update(Integer id, AdminDTO dto) throws Exception {
        Optional<Admin> byUsername = repository.findByUsername(dto.getUsername());

        Optional<Admin> byId = repository.findById(id);
        if (!byId.isPresent()) {
            return new ApiResponse(false, "User not found");
        }


        Admin admin = byId.get();

        admin.setUsername(dto.getUsername());
        admin.setPassword(dto.getPassword());

        try {
            Admin save = repository.save(admin);
            return new ApiResponse(true, "Successfully updated admin", save);
        } catch ( Exception e){
            throw new Exception(e.getMessage());
        }

    }

    @SneakyThrows
    public ApiResponse delete(Integer id) {
        Optional<Admin> byId = repository.findById(id);

        if (byId.isPresent()) {
            repository.deleteById(id);
            return new ApiResponse(true, "delete admin success");
        }
        return new ApiResponse(false, "Admin not found");
    }
}
