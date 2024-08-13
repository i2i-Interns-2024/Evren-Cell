package com.sk.i2i.smsapp.formatter;

import com.sk.i2i.smsapp.model.UserInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageFormatter implements IMessageFormatter {
    @Override
    public String format(UserInfo userInfo) {
        // Local date and time for the current moment
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));

        // Formatting start and end dates
        String startDateTime = userInfo.getSdate();
        String[] startDateTimeParts = startDateTime.split("T");
        String startDate = startDateTimeParts[0];
        String startTime = startDateTimeParts[1].substring(0, 5);

        String endDateTime = userInfo.getEdate();
        String[] endDateTimeParts = endDateTime.split("T");
        String endDate = endDateTimeParts[0];

        return "Değerli kullanıcımız, " + currentDate + " saat " + currentTime
                + " itibariyle, " + endDate + " tarihine kadar geçerli "
                + userInfo.getBalanceData() + " GB internet, "
                + userInfo.getBalanceMinutes() + " dk konuşma ve "
                + userInfo.getBalanceSms() + " adet SMS kullanım hakkınız kalmıştır.";
    }
}
