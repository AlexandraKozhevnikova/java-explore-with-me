package ru.practicum.main_service.service;

import com.querydsl.core.BooleanBuilder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.dto.CompilationRequest;
import ru.practicum.main_service.dto.CompilationResponse;
import ru.practicum.main_service.mapper.CompilationMapper;
import ru.practicum.main_service.model.CompilationEntity;
import ru.practicum.main_service.model.EventEntity;
import ru.practicum.main_service.model.QCompilationEntity;
import ru.practicum.main_service.repository.CompilationRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;

    public CompilationService(CompilationRepository compilationRepository, CompilationMapper compilationMapper,
                              EventService eventService) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.eventService = eventService;
    }

    @Transactional
    public CompilationResponse createCompilation(CompilationRequest request) {
        CompilationEntity compilationNewEntity = compilationMapper.entityFromRequest(request);

        List<EventEntity> events;
        if (!request.getEvents().isEmpty()) {
            events = eventService.checkListEventsIsExistAndGet(request.getEvents());
            if (events.isEmpty()) {
                throw new NoSuchElementException("event " + request.getEvents() + " does not exist.");
            }
            compilationNewEntity.setEvents(events);
        }

        CompilationEntity savedEntity = compilationRepository.save(compilationNewEntity);
        return compilationMapper.responseFromEntity(savedEntity);
    }

    public void deleteCompilation(Long compilationId) {
        try {
            compilationRepository.deleteById(compilationId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Compilation with id=" + compilationId + " was not found");
        }
    }

    @Transactional
    public CompilationResponse updateCompilation(Long compId, CompilationRequest request) {
        checkCompilationIsExistAndGetBasic(compId);
        CompilationEntity savedCompilationEntity = compilationRepository.getEnrichedCompilation(compId);
        CompilationEntity updatedFields = compilationMapper.entityFromRequest(request);

        compilationMapper.updateCompilationEntity(updatedFields, savedCompilationEntity);

        if (!request.getEvents().isEmpty()) {
            List<EventEntity> events = eventService.checkListEventsIsExistAndGet(request.getEvents());
            if (events.isEmpty()) {
                throw new NoSuchElementException("event " + request.getEvents() + " does not exist.");
            }

            savedCompilationEntity.setEvents(events);
        }

        return compilationMapper.responseFromEntity(savedCompilationEntity);
    }

    public CompilationEntity checkCompilationIsExistAndGetBasic(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NoSuchElementException("Compilation with id=" + compId + " was not found"));
    }

    public Set<CompilationResponse> getCompilations(Optional<Boolean> pinned, Integer from, Integer size) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        pinned.ifPresent(aBoolean -> booleanBuilder.and(QCompilationEntity.compilationEntity.isPinned.eq(aBoolean)));
        List<CompilationEntity> compilationEntities = compilationRepository.getCompilations(booleanBuilder, from, size);

        return compilationEntities.stream()
                .map(compilationMapper::responseFromEntity)
                .collect(Collectors.toSet());
    }

    public CompilationResponse getCompilation(Long compId) {
        checkCompilationIsExistAndGetBasic(compId);
        CompilationEntity compilation = compilationRepository.getEnrichedCompilation(compId);
        return compilationMapper.responseFromEntity(compilation);
    }
}
