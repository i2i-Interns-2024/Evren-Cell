package com.sk.i2i.evrencell.mq;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class GenericMessageSerializer<T> implements Serializer<T> {
    private static final Logger logger = LogManager.getLogger(GenericMessageSerializer.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, T data) {
        try {
            if (data == null) {
                logger.info("Null value received during serialisation.");
                return null;
            }
            logger.info("Serialisation...");
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            logger.error("Error during serialisation.", e);
            throw new SerializationException("Error serialising message to byte[] array.");
        }
    }

    @Override
    public void close() {

    }
}
