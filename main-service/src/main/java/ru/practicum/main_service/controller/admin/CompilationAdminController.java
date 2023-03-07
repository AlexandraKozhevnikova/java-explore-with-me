package ru.practicum.main_service.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.dto.CompilationRequest;
import ru.practicum.main_service.dto.CompilationResponse;
import ru.practicum.main_service.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;

    public CompilationAdminController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    public CompilationResponse createCompilation(@RequestBody @Valid CompilationRequest request) {
        return compilationService.createCompilation(request);
    }

    @DeleteMapping("{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("{compId}")
    public CompilationResponse updateCompilation(@PathVariable Long compId,
                                                 @RequestBody CompilationRequest request) {
        return compilationService.updateCompilation(compId, request);
    }


}
