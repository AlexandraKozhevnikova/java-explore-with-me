package ru.practicum.main_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.practicum.statisticclient.StatisticClient;

@SpringBootApplication(scanBasePackages = "ru.practicum.main_service")
public class MainServiceApplication {

    @Bean
    public StatisticClient getStatisticClient(
            @Value("${statistic.server.url}")
            String serverUrl,
            @Value("${spring.application.name}") String appName
    ) {
        return new StatisticClient(serverUrl, appName);
    }

    public static void main(String[] args) {
        SpringApplication.run(MainServiceApplication.class, args);
    }
}
