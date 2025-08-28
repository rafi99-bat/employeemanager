package com.aust.employeemanager.resource;

import com.aust.employeemanager.entity.Employee;
import com.aust.employeemanager.entity.EmployeeImage;
import com.aust.employeemanager.repo.EmployeeImageRepo;
import com.aust.employeemanager.service.EmployeeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/employee/image")
public class EmployeeImageResource {

    private final EmployeeImageRepo imageRepo;
    private final EmployeeService employeeService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public EmployeeImageResource(EmployeeImageRepo imageRepo, EmployeeService employeeService) {
        this.imageRepo = imageRepo;
        this.employeeService = employeeService;
    }

    @PostMapping("/upload/{employeeId}")
    public ResponseEntity<?> uploadImage(@PathVariable Long employeeId, @RequestParam("file") MultipartFile file) {
        try {
            Employee employee = employeeService.findEmployeeById(employeeId);
            if (employee == null) {
                return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
            }

            if (file.getSize() > 50 * 1024) {
                return new ResponseEntity<>("File size must be <= 50KB", HttpStatus.BAD_REQUEST);
            }

            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img == null || img.getWidth() != 300 || img.getHeight() != 300) {
                return new ResponseEntity<>("Image resolution must be 300x300", HttpStatus.BAD_REQUEST);
            }

            if (employee.getImageUrl() != null && !employee.getImageUrl().isEmpty()) {
                String[] parts = employee.getImageUrl().split("/");
                try {
                    Long oldImageId = Long.parseLong(parts[parts.length - 1]);
                    EmployeeImage oldImage = imageRepo.findById(oldImageId).orElse(null);
                    if (oldImage != null) {
                        Path oldPath = Paths.get(uploadDir).resolve(oldImage.getFileName()).normalize();
                        Files.deleteIfExists(oldPath); // delete file
                        imageRepo.delete(oldImage); // delete DB record
                    }
                } catch (NumberFormatException e) {
                }
            }

            String fileName = "emp_" + employeeId + "_" + System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(path);
            Path target = path.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            EmployeeImage empImage = new EmployeeImage(fileName, file.getContentType(), file.getSize(), employee);
            imageRepo.save(empImage);

            String downloadUrl = "/employee/image/download/" + empImage.getId();
            employee.setImageUrl(downloadUrl);
            employeeService.updateEmployee(employee);

            return new ResponseEntity<>("Image uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{imageId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long imageId) throws IOException {
        EmployeeImage empImage = imageRepo.findById(imageId).orElse(null);
        if (empImage == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Path imagePath = Paths.get(uploadDir).resolve(empImage.getFileName()).normalize();
        byte[] imageBytes = Files.readAllBytes(imagePath);

        return ResponseEntity.ok()
                .header("Content-Type", empImage.getFileType())
                .body(imageBytes);
    }

}
