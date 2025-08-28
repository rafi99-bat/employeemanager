package com.aust.employeemanager.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class EmployeeImage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fileName;
    private String fileType;
    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    public EmployeeImage() {}

    public EmployeeImage(String fileName, String fileType, Long fileSize, Employee employee) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.employee = employee;
    }

    public Long getId() { return id; }

    public String getFileName() { return fileName; }

    public String getFileType() { return fileType; }

    public Long getFileSize() { return fileSize; }

    public Employee getEmployee() { return employee; }

    public void setId(Long id) { this.id = id; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public void setFileType(String fileType) { this.fileType = fileType; }

    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public void setEmployee(Employee employee) { this.employee = employee; }
}
