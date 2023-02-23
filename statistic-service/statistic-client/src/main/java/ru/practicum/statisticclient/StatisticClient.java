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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatisticClient {

    private final String serverUrl;
    private final ObjectMapper jsonMapper;
    private final HttpResponse.BodyHandler<String> handler;
    private final HttpClient client;


    public StatisticClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        client = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();
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


        return client.send(request, handler);
    }

    public HttpResponse<String> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris,
                                              Boolean unique)
        throws IOException, InterruptedException {

        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("'start'  and/or 'end' not valid");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm:ss");

        StringBuilder uriBuilder = new StringBuilder(serverUrl + "/stats");
        uriBuilder.append("?start=").append(start.format(formatter));
        uriBuilder.append("&end=").append(end.format(formatter));

        if (unique) {
            uriBuilder.append("&unique=true");
        }

        if (uris != null && !uris.isEmpty()) {
            if (uris.size() > 20) {
                throw new IllegalArgumentException("'uris' is too much");
            }
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(uriBuilder.toString()))
            .version(HttpClient.Version.HTTP_1_1)
            .header("Accept", "application/json")
            .build();


        return client.send(request, handler);
    }
}
