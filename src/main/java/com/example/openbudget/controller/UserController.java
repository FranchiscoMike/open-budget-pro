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
     * get one
     * @return
     */
    @GetMapping("/{id}")
    private HttpEntity<?> findOne(@PathVariable Integer id){
        ApiResponse one = userService.findOne(id);
        return ResponseEntity.status(200).body(one);
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

    /**
     * all verified users
     * @return
     */
    @GetMapping("/verified_users")
    private HttpEntity<?> findAllVerifiedUsers(){
        ApiResponse all = userService.findAllVerifiedUsers();
        return ResponseEntity.status(200).body(all);
    }

    /**
     * paid users
     * @return
     */
    @GetMapping("/paid_users")
    private HttpEntity<?> paid_users(){
        ApiResponse paid_users = userService.paid_users();
        return ResponseEntity.status(200).body(paid_users);
    }

    @PostMapping("/send_code/{phone}")
    private HttpEntity<?> askingCode(@PathVariable("phone") String phone){
        ApiResponse askingCode = userService.askingCode(phone);
        return ResponseEntity.status(200).body(askingCode);
    }


}
