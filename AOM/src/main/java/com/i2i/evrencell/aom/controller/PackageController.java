package com.i2i.evrencell.aom.controller;

import com.i2i.evrencell.aom.dto.PackageDetails;
import com.i2i.evrencell.aom.dto.PackageDto;
import com.i2i.evrencell.aom.service.PackageService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for Package related operations
 */
@RestController
@RequestMapping("/v1/api/packages")
public class PackageController {

    private final PackageService packageService;
    private static final Logger logger = LogManager.getLogger(PackageController.class);

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping("/getAllPackages")
    public ResponseEntity<List<PackageDto>> getAllPackages (){
        logger.debug("Request is taken, getting all packages");
        return ResponseEntity.ok(packageService.getAllPackages());
    }

    @GetMapping("/getUserPackageByMsisdn")
    public ResponseEntity<PackageDto> getUserPackageByMsisdn (@RequestParam String msisdn){
        logger.debug("Request is taken, getting user package by MSISDN: " + msisdn);
        return ResponseEntity.ok(packageService.getUserPackageByMsisdn(msisdn));
    }

    @GetMapping("/getPackageDetails")
    public ResponseEntity<Optional<PackageDetails>> getPackageDetails (@RequestParam String packageName){
        logger.debug("Request is taken, getting package details for package: " + packageName);
        return ResponseEntity.ok(packageService.getPackageDetails(packageName));
    }
}
