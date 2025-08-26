package com.aust.employeemanager.service;

import com.aust.employeemanager.entity.AppUser;
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

    public int createUser(String username, String rawPassword) {
        if (username.equals("admin")) {
            return 1;
        }
        AppUser existingUser = userRepository.findByUsername(username);

        if (existingUser != null) {
            return 2;
        } else {
            // Create new user
            AppUser newUser = new AppUser();
            newUser.setUsername(username);
            newUser.setPassword(passwordEncoder.encode(rawPassword));
            newUser.setRole("ROLE_USER");
            userRepository.save(newUser);
            return 0;
        }
    }
}
