package ru.practicum.statisticclient;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import statisticcommon.HitRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Service
public class StatisticClient {

    private final String serverUrl;
    private final ObjectMapper jsonMapper;


    public StatisticClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());
    }

    public HttpResponse<String> addHit(String app, String uri, String ip, LocalDateTime timestamp)
        throws IOException, InterruptedException {
        HitRequest body = new HitRequest();
        body.setApp(app);
        body.setUri(uri);
        body.setIp(ip);
        body.setTimestamp(timestamp);

        HttpRequest request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(body)))
            .uri(URI.create(serverUrl + "/hit"))
            .version(HttpClient.Version.HTTP_1_1)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        return client.send(request, handler);
    }
}
