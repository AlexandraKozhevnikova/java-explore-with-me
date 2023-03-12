package ru.practicum.main_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Explore With Me",
                description = "Search your event", version = "1.0.0",
                contact = @Contact(
                        name = "Kozhevnikova Alexandra"
                )
        )
)
public class OpenApiConfig {
}
