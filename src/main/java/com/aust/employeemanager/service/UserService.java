package com.aust.employeemanager.service;

import com.aust.employeemanager.model.AppUser;
import com.aust.employeemanager.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(String username, String rawPassword, String role) {
        AppUser existingUser = userRepository.findByUsername(username);

        if (existingUser != null) {
            System.out.println("Username already exists: " + username);
        } else {
            // Create new user
            AppUser newUser = new AppUser();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(rawPassword));
            newUser.setRole(role);
            userRepository.save(newUser);
            System.out.println("Created new user: " + username);
        }
    }
}
