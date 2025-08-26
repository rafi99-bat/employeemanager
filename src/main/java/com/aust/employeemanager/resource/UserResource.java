package com.aust.employeemanager.resource;

import com.aust.employeemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserResource {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> registerUser(
            @RequestParam String username,
            @RequestParam String password) {
        ResponseEntity<String> response = null;
        try {
            int flag = userService.createUser(username, password);
            if (flag == 0){
                response = ResponseEntity.ok("User registered successfully!");
            } else if (flag == 1){
                response = ResponseEntity.status(403).body("Username cannot be admin!");
            } else if (flag == 2) {
                response = ResponseEntity.status(401).body("Username already exists!");
            }
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}
