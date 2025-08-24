package com.aust.employeemanager.repo;

import com.aust.employeemanager.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}