package com.aust.employeemanager.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
public class LoginResource {

    @GetMapping("/auth/login")
    public ResponseEntity<?> login(@RequestParam String role, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body("Authentication failed");
        }

        // Get roles of authenticated user
        String userRoles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        if (userRoles.contains("ROLE_" + role)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(403).body("Role mismatch");
        }
    }
}
