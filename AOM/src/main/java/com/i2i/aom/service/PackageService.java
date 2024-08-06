package com.i2i.aom.service;

import com.i2i.aom.dto.PackageDto;
import com.i2i.aom.mapper.PackageMapper;
import com.i2i.aom.model.Package;
import com.i2i.aom.repository.PackageRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class PackageService {
    private final PackageRepository packageRepository;
    private final PackageMapper packageMapper;

    public PackageService(PackageRepository packageRepository, PackageMapper packageMapper) {
        this.packageRepository = packageRepository;
        this.packageMapper = packageMapper;
    }

    public List<PackageDto> getAllPackages() {
        try {
            List<Package> packages = packageRepository.getAllPackages();
            return packages.stream()
                    .map(packageMapper::packageToPackageDto)
                    .toList();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error retrieving packages", e);
        }
    }


}
