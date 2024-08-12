package com.i2i.evrencell.aom.mapper;

import com.i2i.evrencell.aom.dto.PackageDto;
import com.i2i.evrencell.voltdb.Package;
import org.springframework.stereotype.Component;

@Component
public class PackageMapper {

    public PackageDto voltPackageToPackageDto(Package packageModel){
        return PackageDto.builder()
                .packageId(packageModel.getPackageId())
                .packageName(packageModel.getPackageName())
                .amountMinutes(packageModel.getAmountMinutes())
                .price(packageModel.getPrice())
                .amountData(packageModel.getAmountData())
                .amountSms(packageModel.getAmountSms())
                .period(packageModel.getPeriod())
                .build();
    }

    public PackageDto packageToPackageDto(com.i2i.evrencell.aom.model.Package packageModel){
        return PackageDto.builder()
                .packageId(packageModel.getPackageId())
                .packageName(packageModel.getPackageName())
                .amountMinutes(packageModel.getAmountMinutes())
                .price(packageModel.getPrice())
                .amountData(packageModel.getAmountData())
                .amountSms(packageModel.getAmountSms())
                .period(packageModel.getPeriod())
                .build();
    }

}
