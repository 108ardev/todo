package pro.ardev.todo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import pro.ardev.todo.model.dto.CreateTaskRequestDto;
import pro.ardev.todo.model.dto.TaskResponseDto;
import pro.ardev.todo.model.dto.UpdateTaskRequestDto;
import pro.ardev.todo.model.entity.Task;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

    Task toEntity(CreateTaskRequestDto request);

    TaskResponseDto toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Task entity, UpdateTaskRequestDto dto);
}
