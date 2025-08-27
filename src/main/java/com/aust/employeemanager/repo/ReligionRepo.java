package com.aust.employeemanager.repo;

import com.aust.employeemanager.entity.Religion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReligionRepo extends JpaRepository<Religion, Long> {
    Optional<Religion> findByName(String name);
}
