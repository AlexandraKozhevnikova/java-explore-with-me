package ru.practicum.statisticserver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.dto.HitRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonTest
public class SerializationTest {
    private final JacksonTester<HitRequest> jacksonRequest;
    public static final String REQUEST = "{\"app\":\"ewm-main-service\",\"uri\":\"/events/1\",\"ip\":\"192.163.0.1\"," +
        "\"timestamp\":\"2022-09-06 11:00:23\"}";

    @Autowired
    public SerializationTest(JacksonTester<HitRequest> jacksonRequest) {
        this.jacksonRequest = jacksonRequest;
    }

    @Test
    void serialize_hitRequest() throws IOException {
        HitRequest request = new HitRequest();
        request.setApp("ewm-main-service");
        request.setUri("/events/1");
        request.setIp("192.163.0.1");
        request.setTimestamp(LocalDateTime.parse("2022-09-06 11:00:23",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        JsonContent<HitRequest> result = jacksonRequest.write(request);

        Assertions.assertEquals(REQUEST, result.getJson());
    }

    @Test
    void deserialize_hitRequest() throws IOException {
        HitRequest request = jacksonRequest.parseObject(REQUEST);

        Assertions.assertEquals("ewm-main-service", request.getApp());
        Assertions.assertEquals("/events/1", request.getUri());
        Assertions.assertEquals("192.163.0.1", request.getIp());
        Assertions.assertEquals("2022-09-06T11:00:23", request.getTimestamp().toString());
    }
}
