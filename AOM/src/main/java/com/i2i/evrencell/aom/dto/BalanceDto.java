package com.i2i.evrencell.aom.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public record BalanceDto(
         Integer    balanceId,
         Integer    packageId,
         Integer    customerId,
         Integer    partitionId,
         Integer    balanceLevelMinutes,
         Integer    balanceLevelSMS,
         Integer    balanceLevelData,
         Integer    balanceLevelMoney,
         Date       sDate,
         Date       eDate,
         long       price
) { }
