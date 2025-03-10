package com.capstone.controller;

import com.capstone.model.User;
import com.capstone.model.Student;
import com.capstone.model.Faculty;
import com.capstone.model.Admin;
import com.capstone.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 2. Get user by ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable String id) {
        return userRepository.findById(id);
    }

    // 3. Add a new user (Student, Faculty, or Admin)
    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // 4. Update a user
    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        if (userRepository.existsById(id)) {
            updatedUser.setUserID(id);
            return userRepository.save(updatedUser);
        }
        return null;
    }

    // 5. Delete a user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);
    }
}
