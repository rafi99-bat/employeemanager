package com.aust.employeemanager.service;

import com.aust.employeemanager.entity.EmployeeImage;
import com.aust.employeemanager.entity.Religion;
import com.aust.employeemanager.exception.UserNotFoundException;
import com.aust.employeemanager.entity.Employee;
import com.aust.employeemanager.repo.EmployeeImageRepo;
import com.aust.employeemanager.repo.EmployeeRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.nio.file.*;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepo employeeRepo;
    private final EmployeeImageRepo imageRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private ReligionService religionService;

    @Autowired
    public EmployeeService(EmployeeRepo employeeRepo, EmployeeImageRepo imageRepo) {
        this.employeeRepo = employeeRepo;
        this.imageRepo = imageRepo;
    }

    public Employee addEmployee(Employee employee) {
        employee.setEmployeeCode(UUID.randomUUID().toString());
        if (employee.getReligion() != null && employee.getReligion().getName() != null) {
            Religion religion = religionService.addReligion(employee.getReligion().getName());
            employee.setReligion(religion);
        }
        return employeeRepo.save(employee);
    }

    public List<Employee> findAllEmployees() {
        return employeeRepo.findAll();
    }

    public Employee updateEmployee(Employee employee) {
        Employee existingEmployee = employeeRepo.findById(employee.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employee.getId()));

        existingEmployee.setId(existingEmployee.getId());
        existingEmployee.setName(employee.getName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setJobTitle(employee.getJobTitle());
        existingEmployee.setPhone(employee.getPhone());
        existingEmployee.setReligion(employee.getReligion());
        existingEmployee.setImageUrl(employee.getImageUrl());
        existingEmployee.setEmployeeCode(existingEmployee.getEmployeeCode());

        return employeeRepo.save(existingEmployee);
    }

    public Employee findEmployeeById(Long id) {
        return employeeRepo.findEmployeeById(id)
                .orElseThrow(() -> new UserNotFoundException("User by id " + id + " was not found"));
    }

    public void deleteEmployee(Long id) {
        Employee emp = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<EmployeeImage> images = imageRepo.findAllByEmployee(emp);
        for (EmployeeImage img : images) {
            try {
                Path path = Paths.get(uploadDir).resolve(img.getFileName()).normalize();
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("Failed to delete image file: " + img.getFileName());
            }
        }

        imageRepo.deleteAllByEmployee(emp);
        employeeRepo.delete(emp);
    }

}
