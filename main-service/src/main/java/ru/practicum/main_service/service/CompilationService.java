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

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.HashSet;
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

        Set<EventEntity> events;
        if (request.getEvents() != null && !request.getEvents().isEmpty()) {
            events = new HashSet<>(eventService.checkListEventsIsExistAndGet(request.getEvents()));
            if (events.isEmpty()) {
                throw new NoSuchElementException("event " + request.getEvents() + " does not exist.");
            }
            compilationNewEntity.setEvents(events);
        }
        return compilationMapper.responseFromEntity(compilationRepository.save(compilationNewEntity));
    }

    @Transactional
    public void deleteCompilation(Long compilationId) {
        try {
            compilationRepository.deleteById(compilationId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Compilation with id=" + compilationId + " was not found");
        }
    }

    @Transactional
    public CompilationResponse updateCompilation(Long compId, CompilationRequest request) {
        if (request.getTitle() != null) {
            if (request.getTitle().length() > 121) {
                throw new ValidationException("title must not be more than 120 symbols");
            }
        }
        CompilationEntity savedCompilationEntity = checkCompilationIsExistAndGetBasic(compId);

        if (request.getEvents() != null) {
            if (!request.getEvents().isEmpty()) {
                savedCompilationEntity = compilationRepository.getEnrichedCompilation(compId);

                if (!request.getEvents().equals(savedCompilationEntity.getEvents().stream()
                        .map(EventEntity::getEventId)
                        .collect(Collectors.toList()))) {

                    Set<EventEntity> actualEvents = new HashSet<>(eventService
                            .checkListEventsIsExistAndGet(request.getEvents()));

                    if (actualEvents.isEmpty()) {
                        throw new NoSuchElementException("event(s) " + request.getEvents() + " does not exist.");
                    }

                    if (!savedCompilationEntity.getEvents().equals(actualEvents)) {
                        Set<EventEntity> existEvents = savedCompilationEntity.getEvents();
                        existEvents.removeIf(it -> !actualEvents.contains(it));
                        existEvents.addAll(actualEvents);
                    }
                }
            } else {
                savedCompilationEntity.setEvents(Collections.EMPTY_SET);
            }
        }

        CompilationEntity updatedFields = compilationMapper.entityFromRequest(request);

        compilationMapper.updateCompilationEntity(updatedFields, savedCompilationEntity);

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
