package ru.practicum.conf;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration

public class AppConfig {
    @Bean
    public JPAQueryFactory createJPAQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
