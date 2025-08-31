package com.aust.employeemanager.service;

import com.aust.employeemanager.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, String> authenticateUser(String username, String password) throws AuthenticationException {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        String userRole = auth.getAuthorities().iterator().next().getAuthority();
        String token = jwtUtil.generateToken(username, userRole);

        return Map.of("token", token);
    }
}
