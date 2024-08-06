package com.i2i.aom.mapper;

import com.i2i.aom.dto.PackageDto;
import com.i2i.aom.model.Package;
import org.springframework.stereotype.Component;

@Component
public class PackageMapper {

    public PackageDto packageToPackageDto(Package packageModel){
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
