package com.i2i.evrencell.kafka.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class GenericMessageDeserializer<T> implements Deserializer<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> targetClass;

    public GenericMessageDeserializer(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing message", e);
        }
    }
}
