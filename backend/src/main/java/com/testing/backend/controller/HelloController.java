package com.testing.backend.controller;

import com.testing.backend.entity.UserEntity;
import com.testing.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class HelloController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from Spring Boot!");
        return response;
    }

    @GetMapping("/users")
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public UserEntity createUser(@RequestBody UserEntity user) {
        return userRepository.save(user);
    }
}