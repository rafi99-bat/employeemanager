package com.aust.employeemanager.service;

import com.aust.employeemanager.entity.Religion;
import com.aust.employeemanager.repo.ReligionRepo;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReligionService {
    private final ReligionRepo religionRepo;

    public ReligionService(ReligionRepo religionRepo) {
        this.religionRepo = religionRepo;
    }

    public List<Religion> getAllReligions() {
        return religionRepo.findAll();
    }

    public Religion addReligion(String name) {
        return religionRepo.findByName(name)
                .orElseGet(() -> religionRepo.save(new Religion(name)));
    }
}
