package com.i2i.evrencell.aom.mapper;

import com.i2i.evrencell.aom.dto.BalanceDto;
import com.i2i.evrencell.aom.model.Balance;
import org.springframework.stereotype.Component;

@Component
public class BalanceMapper {

    public BalanceDto balanceToBalanceDto(Balance balance){
        return BalanceDto.builder()
                .balanceId(balance.getBalanceId())
                .packageId(balance.getPackageId())
                .customerId(balance.getCustomerId())
                .partitionId(balance.getPartitionId())
                .balanceLevelMinutes(balance.getBalanceLevelMinutes())
                .balanceLevelSMS(balance.getBalanceLevelSMS())
                .balanceLevelData(balance.getBalanceLevelData())
                .balanceLevelMoney(balance.getBalanceLevelMoney())
                .sDate(balance.getsDate())
                .eDate(balance.geteDate())
                .price(balance.getPrice())
                .build();
    }

}
