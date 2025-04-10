package com.capstone.service;

import com.capstone.model.Admin;
import com.capstone.model.User;
import com.capstone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public Optional<Admin> getAdminById(String adminId) {
        Optional<User> userOpt = userRepository.findByUserID(adminId);
        if (userOpt.isPresent() && userOpt.get() instanceof Admin) {
            return Optional.of((Admin) userOpt.get());
        }
        return Optional.empty();
    }

    // Add more admin-related logic here later if needed
}
