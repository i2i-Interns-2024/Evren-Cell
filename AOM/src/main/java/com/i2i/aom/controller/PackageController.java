package com.i2i.aom.controller;

import com.i2i.aom.dto.PackageDto;
import com.i2i.aom.service.PackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/packages")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping("/getAllPackages")
    public ResponseEntity<List<PackageDto>> getAllPackages (){
        return ResponseEntity.ok(packageService.getAllPackages());
    }
}
