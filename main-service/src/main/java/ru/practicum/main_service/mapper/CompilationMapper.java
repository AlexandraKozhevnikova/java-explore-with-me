package ru.practicum.main_service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.main_service.dto.CompilationRequest;
import ru.practicum.main_service.dto.CompilationResponse;
import ru.practicum.main_service.model.CompilationEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = EventMapper.class)
public interface CompilationMapper {

    @Mapping(target = "compilationId", ignore = true)
    @Mapping(target = "events", ignore = true)
    CompilationEntity entityFromRequest(CompilationRequest request);

    @Mapping(target = "id", source = "compilationId")
    CompilationResponse responseFromEntity(CompilationEntity save);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "compilationId", ignore = true)
    CompilationEntity updateCompilationEntity(CompilationEntity updatedFields,
                                              @MappingTarget CompilationEntity savedCompilationEntity);
}
