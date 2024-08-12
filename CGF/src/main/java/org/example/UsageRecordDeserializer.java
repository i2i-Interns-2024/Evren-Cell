package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class UsageRecordDeserializer implements Deserializer<UsageRecord> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UsageRecord deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, UsageRecord.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing OperationMessage", e);
        }
    }
}
