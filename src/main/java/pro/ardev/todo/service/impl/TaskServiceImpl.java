package pro.ardev.todo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.ardev.todo.exception.ResourceNotFoundException;
import pro.ardev.todo.mapper.TaskMapper;
import pro.ardev.todo.model.dto.CreateTaskRequestDto;
import pro.ardev.todo.model.dto.TaskResponseDto;
import pro.ardev.todo.model.dto.UpdateTaskRequestDto;
import pro.ardev.todo.model.entity.Task;
import pro.ardev.todo.repository.TaskRepository;
import pro.ardev.todo.service.TaskService;

import java.util.List;

import static pro.ardev.todo.model.enums.TaskStatus.TODO;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTasks() {
        return this.taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public TaskResponseDto createTask(CreateTaskRequestDto createTaskRequestDto) {
        Task task = this.taskMapper.toEntity(createTaskRequestDto);
        task.setStatus(TODO);
        Task saved = this.taskRepository.save(task);

        return this.taskMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Task with ID %d not found", id)));

        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        if (!this.taskRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format("Task with ID %d not found", id));
        }
        this.taskRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long id, UpdateTaskRequestDto updateTaskRequestDto) {
        Task task = this.taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Task with ID %d not found", id)));

        this.taskMapper.updateEntity(task, updateTaskRequestDto);

        this.taskRepository.save(task);
        return this.taskMapper.toDto(task);
    }
}
