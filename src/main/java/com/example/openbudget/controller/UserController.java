package com.example.openbudget.controller;

import com.example.openbudget.dto.ApiResponse;
import com.example.openbudget.dto.ProjectDTO;
import com.example.openbudget.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin()
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * all users
     * @return
     */
    @GetMapping()
    private HttpEntity<?> findAll(){
        ApiResponse all = userService.findAll();
        return ResponseEntity.status(200).body(all);
    }

    /**
     * all new registered users
     * @return
     */
    @GetMapping("/new_users")
    private HttpEntity<?> findAllNewUsers(){
        ApiResponse all = userService.findNewUsers();
        return ResponseEntity.status(200).body(all);
    }

    @PostMapping("/send_code/{phone}")
    private HttpEntity<?> askingCode(@PathVariable("phone") String phone){
        ApiResponse askingCode = userService.askingCode(phone);
        return ResponseEntity.status(200).body(askingCode);
    }



}
