package com.aust.employeemanager.resource;

import com.aust.employeemanager.entity.Employee;
import com.aust.employeemanager.entity.EmployeeImage;
import com.aust.employeemanager.repo.EmployeeImageRepo;
import com.aust.employeemanager.service.EmployeeImageService;
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

    private final EmployeeImageService imageService;

    public EmployeeImageResource(EmployeeImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload/{employeeId}")
    public ResponseEntity<?> uploadImage(@PathVariable Long employeeId,
                                         @RequestParam("file") MultipartFile file) {
        try {
            String downloadUrl = imageService.uploadEmployeeImage(employeeId, file);
            return ResponseEntity.ok("Image uploaded successfully. Download URL: " + downloadUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @GetMapping("/download/{imageId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long imageId) {
        try {
            byte[] imageBytes = imageService.getImageBytes(imageId);
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .body(imageBytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
