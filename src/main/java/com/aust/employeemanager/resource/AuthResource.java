package com.aust.employeemanager.resource;

import com.aust.employeemanager.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthResource {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthResource(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String role = body.get("role"); // new: send role from frontend

        try {
            // Authenticate username/password first
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Verify that the user's role matches what was sent
            String userRole = auth.getAuthorities().iterator().next().getAuthority();
            if (!userRole.equals(role)) {
                return ResponseEntity.status(403).body("Role does not match credentials");
            }

            // Generate JWT token with username and role
            String token = jwtUtil.generateToken(username, userRole);

            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
