package ru.practicum.mainservice.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.micrometer.core.instrument.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDataTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        String value = jsonParser.getText();
        value = value.trim();
        if (!StringUtils.isBlank(value)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);
            return LocalDateTime.parse(value, formatter);
        }
        return null;
    }
}

