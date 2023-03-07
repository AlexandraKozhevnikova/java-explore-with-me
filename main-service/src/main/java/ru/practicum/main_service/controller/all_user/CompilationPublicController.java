package ru.practicum.main_service.controller.all_user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.dto.CompilationResponse;
import ru.practicum.main_service.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/compilations")
public class CompilationPublicController {

    private final CompilationService compilationService;

    public CompilationPublicController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    public Set<CompilationResponse> getCompilations(@RequestParam Optional<Boolean> pinned,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationResponse getCompilation(@PathVariable Long compId) {
        return compilationService.getCompilation(compId);
    }
}
