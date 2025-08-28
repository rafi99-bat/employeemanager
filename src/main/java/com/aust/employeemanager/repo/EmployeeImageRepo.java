package com.aust.employeemanager.repo;

import com.aust.employeemanager.entity.Employee;
import com.aust.employeemanager.entity.EmployeeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeImageRepo extends JpaRepository<EmployeeImage, Long> {
    void deleteAllByEmployee(Employee employee);

    List<EmployeeImage> findByEmployeeId(Long employeeId);
    List<EmployeeImage> findAllByEmployee(Employee employee);
}
