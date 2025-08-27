package com.aust.employeemanager.resource;

import com.aust.employeemanager.entity.Religion;
import com.aust.employeemanager.repo.ReligionRepo;
import com.aust.employeemanager.service.ReligionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/religion")
@CrossOrigin(origins = "http://localhost:4200")
public class ReligionResource {
    private final ReligionService religionService;

    public ReligionResource(ReligionService religionService) { this.religionService = religionService; }

    @GetMapping("/all")
    public ResponseEntity<List<Religion>> getAllReligions() {
        return new ResponseEntity<>(religionService.getAllReligions(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Religion> addReligion(@RequestBody Religion religion) {
        Religion newReligion = religionService.addReligion(religion.getName());
        return new ResponseEntity<>(newReligion, HttpStatus.CREATED);
    }
}
