package ru.practicum.all;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.HitRequest;

import javax.validation.Valid;

@RestController
public class StatsController {
    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @PostMapping(path = "/hit")
    public void createHit(@Valid @RequestBody HitRequest body) {
        service.createHit(body);
    }
}
