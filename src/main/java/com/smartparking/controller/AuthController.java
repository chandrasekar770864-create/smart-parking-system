package com.smartparking.controller;

import com.smartparking.dto.ApiResponse;
import com.smartparking.dto.LoginRequest;
import com.smartparking.dto.RegisterRequest;
import com.smartparking.model.User;
import com.smartparking.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        Optional<User> userOpt = userService.authenticate(req);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid username or password"));
        }

        User user = userOpt.get();
        session.setAttribute("user", user);

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("fullName", user.getFullName());
        data.put("email", user.getEmail());
        data.put("role", user.getRole());

        return ResponseEntity.ok(ApiResponse.ok("Login successful! Welcome " + user.getFullName(), data));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest req, HttpSession session) {
        try {
            User user = userService.registerUser(req);
            session.setAttribute("user", user);

            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("fullName", user.getFullName());
            data.put("email", user.getEmail());
            data.put("role", user.getRole());

            return ResponseEntity.ok(ApiResponse.ok("Registration successful!", data));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> currentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok(ApiResponse.ok("Guest user", null));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("fullName", user.getFullName());
        data.put("email", user.getEmail());
        data.put("role", user.getRole());

        return ResponseEntity.ok(ApiResponse.ok("Current user profile", data));
    }
}
