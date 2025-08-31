package com.aust.employeemanager.service;

import com.aust.employeemanager.entity.Employee;
import com.aust.employeemanager.entity.EmployeeImage;
import com.aust.employeemanager.repo.EmployeeImageRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class EmployeeImageService {

    private final EmployeeImageRepo imageRepo;
    private final EmployeeService employeeService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public EmployeeImageService(EmployeeImageRepo imageRepo, EmployeeService employeeService) {
        this.imageRepo = imageRepo;
        this.employeeService = employeeService;
    }

    public String uploadEmployeeImage(Long employeeId, MultipartFile file) throws IOException {
        Employee employee = employeeService.findEmployeeById(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found");
        }

        // validation
        if (file.getSize() > 50 * 1024) {
            throw new IllegalArgumentException("File size must be <= 50KB");
        }
        BufferedImage img = ImageIO.read(file.getInputStream());
        if (img == null || img.getWidth() != 300 || img.getHeight() != 300) {
            throw new IllegalArgumentException("Image resolution must be 300x300");
        }

        // delete old image if exists
        if (employee.getImageUrl() != null && !employee.getImageUrl().isEmpty()) {
            String[] parts = employee.getImageUrl().split("/");
            try {
                Long oldImageId = Long.parseLong(parts[parts.length - 1]);
                imageRepo.findById(oldImageId).ifPresent(oldImage -> {
                    try {
                        Files.deleteIfExists(Paths.get(uploadDir).resolve(oldImage.getFileName()));
                        imageRepo.delete(oldImage);
                    } catch (IOException ignored) {}
                });
            } catch (NumberFormatException ignored) {}
        }

        // save new file
        String fileName = "emp_" + employeeId + "_" + System.currentTimeMillis() + "_" +
                StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(path);
        Path target = path.resolve(fileName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        EmployeeImage empImage = new EmployeeImage(fileName, file.getContentType(), file.getSize(), employee);
        imageRepo.save(empImage);

        String downloadUrl = "/employee/image/download/" + empImage.getId();
        employee.setImageUrl(downloadUrl);
        employeeService.updateEmployee(employee);

        return downloadUrl;
    }

    public byte[] getImageBytes(Long imageId) throws IOException {
        EmployeeImage empImage = imageRepo.findById(imageId).orElse(null);
        if (empImage == null) {
            throw new IllegalArgumentException("Image not found");
        }
        Path imagePath = Paths.get(uploadDir).resolve(empImage.getFileName()).normalize();
        return Files.readAllBytes(imagePath);
    }
}
