package com.i2i.evrencell.aom.service;

import com.i2i.evrencell.aom.dto.PackageDetails;
import com.i2i.evrencell.aom.dto.PackageDto;
import com.i2i.evrencell.aom.mapper.PackageMapper;
import com.i2i.evrencell.aom.repository.PackageRepository;
import com.i2i.evrencell.voltdb.VoltPackage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.voltdb.client.ProcCallException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class PackageService {
    private final PackageRepository packageRepository;
    private final PackageMapper packageMapper;
    private static final Logger logger = LogManager.getLogger(PackageService.class);

    public PackageService(PackageRepository packageRepository,
                          PackageMapper packageMapper) {
        this.packageRepository = packageRepository;
        this.packageMapper = packageMapper;
    }

    public List<PackageDto> getAllPackages() {
        try {
            List<com.i2i.evrencell.aom.model.Package> packages = packageRepository.getAllPackages();
            return packages.stream()
                    .map(packageMapper::packageToPackageDto)
                    .toList();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error retrieving packages", e);
        }
    }

    public PackageDto getUserPackageByMsisdn(String msisdn){
        try {
            logger.debug("Getting user package by MSISDN: " + msisdn);
            VoltPackage packageModel = packageRepository.getUserPackageByMsisdn(msisdn);
            logger.debug("User package found by MSISDN: " + msisdn);
            return packageMapper.voltPackageToPackageDto(packageModel);

        }catch (IOException | ProcCallException exception){
            throw new RuntimeException("Error retrieving user package by msisdn ", exception);
        }
    }

    public Optional<PackageDetails> getPackageDetails(String packageName){
        try {
            return packageRepository.getPackageDetails(packageName);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error retrieving package details", e);
        }
    }
}
