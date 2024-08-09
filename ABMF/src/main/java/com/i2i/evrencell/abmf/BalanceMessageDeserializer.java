package com.i2i.evrencell.abmf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class BalanceMessageDeserializer implements Deserializer<BalanceMessage> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BalanceMessage deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, BalanceMessage.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing BalanceMessage", e);
        }
    }
}
