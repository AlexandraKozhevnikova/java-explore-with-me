package ru.practicum.main_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.dto.CompilationRequest;
import ru.practicum.main_service.dto.CompilationResponse;
import ru.practicum.main_service.mapper.CompilationMapper;
import ru.practicum.main_service.model.CompilationEntity;
import ru.practicum.main_service.model.EventEntity;
import ru.practicum.main_service.repository.CompilationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    private final EventService eventService;

    public CompilationService(CompilationRepository compilationRepository, CompilationMapper compilationMapper, EventService eventService) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.eventService = eventService;
    }

    @Transactional
    public CompilationResponse createCompilation(CompilationRequest request) {
        CompilationEntity compilationNewEntity = compilationMapper.entityFromRequest(request);

        List<EventEntity> events = new ArrayList<>();
        if (!request.getEvents().isEmpty()) {
            events = eventService.checkListEventsIsExistAndGet(request.getEvents());
            if (events.isEmpty()){
                throw new NoSuchElementException("event " + request.getEvents() + " does not exist.") ;
            }
            compilationNewEntity.setEvents(events);
        }

        CompilationEntity savedEntity = compilationRepository.save(compilationNewEntity);
     //   savedEntity.setEvents(events);
        return compilationMapper.responseFromEntity(savedEntity);
    }
}
