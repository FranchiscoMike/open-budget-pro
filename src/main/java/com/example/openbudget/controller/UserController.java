package com.example.openbudget.controller;

import com.example.openbudget.dto.ApiResponse;
import com.example.openbudget.service.UserService;
import lombok.RequiredArgsConstructor;
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
     *
     * @return
     */
    @GetMapping()
    private HttpEntity<?> findAll() {
        ApiResponse all = userService.findAll();
        return ResponseEntity.status(200).body(all);
    }

    /**
     * get one
     *
     * @return
     */
    @GetMapping("/{id}")
    private HttpEntity<?> findOne(@PathVariable Integer id) {
        ApiResponse one = userService.findOne(id);
        return ResponseEntity.status(200).body(one);
    }

    /**
     * all new registered users
     *
     * @return
     */
    @GetMapping("/new_users")
    private HttpEntity<?> findAllNewUsers() {
        ApiResponse all = userService.findNewUsers();
        return ResponseEntity.status(200).body(all);
    }

    /**
     * all verified users
     *
     * @return
     */
    @GetMapping("/verified_users")
    private HttpEntity<?> findAllVerifiedUsers() {
        ApiResponse all = userService.findAllVerifiedUsers();
        return ResponseEntity.status(200).body(all);
    }

    /**
     * paid users
     *
     * @return
     */
    @GetMapping("/paid_users")
    private HttpEntity<?> paid_users() {
        ApiResponse paid_users = userService.paid_users();
        return ResponseEntity.status(200).body(paid_users);
    }

    @PostMapping("/send_code/{phone}")
    private HttpEntity<?> askingCode(@PathVariable("phone") String phone) {
        ApiResponse askingCode = userService.askingCode(phone);
        return ResponseEntity.status(200).body(askingCode);
    }

    /**
     * verify user
     */
    @PostMapping("/verify/{phone}")
    private HttpEntity<?> verify_user(@PathVariable("phone") String phone) {
        ApiResponse verify_user = userService.verify_user(phone);
        return ResponseEntity.status(200).body(verify_user);
    }

    /**
     * resend code
     */
    @PostMapping("/resend_code/{phone}")
    private HttpEntity<?> resend_code(@PathVariable("phone") String phone) {
        ApiResponse resend_code = userService.resend_code(phone);
        return ResponseEntity.status(200).body(resend_code);
    }

    /**
     * code is not received
     */
    @PostMapping("/code_not_received/{phone}")
    private HttpEntity<?> code_not_received(@PathVariable("phone") String phone) {
        ApiResponse code_not_received = userService.code_not_received(phone);
        return ResponseEntity.status(200).body(code_not_received);
    }

    /**
     * user is paid successfully
     */
    @PostMapping("/user_is_paid/{phone}")
    private HttpEntity<?> user_is_paid(@PathVariable("phone") String phone) {
        ApiResponse user_is_paid = userService.user_is_paid(phone);
        return ResponseEntity.status(200).body(user_is_paid);
    }

    /**
     * this user is not verified
     */
    @PostMapping("/user_not_verified/{phone}")
    private HttpEntity<?> user_not_verified(@PathVariable("phone") String phone) {
        ApiResponse user_not_verified = userService.user_not_verified(phone);
        return ResponseEntity.status(200).body(user_not_verified);
    }

}
