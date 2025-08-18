package com.aust.employeemanager.service;

import com.aust.employeemanager.exception.UserNotFoundException;
import com.aust.employeemanager.model.Employee;
import com.aust.employeemanager.repo.EmployeeRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepo employeeRepo;

    @Autowired
    public EmployeeService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    public Employee addEmployee(Employee employee) {
        employee.setEmployeeCode(UUID.randomUUID().toString());
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
        existingEmployee.setImageUrl(employee.getImageUrl());
        existingEmployee.setEmployeeCode(existingEmployee.getEmployeeCode());

        return employeeRepo.save(existingEmployee);
    }

    public Employee findEmployeeById(Long id) {
        return employeeRepo.findEmployeeById(id)
                .orElseThrow(() -> new UserNotFoundException("User by id " + id + " was not found"));
    }

    public void deleteEmployee(Long id){
        employeeRepo.deleteEmployeeById(id);
    }
}
