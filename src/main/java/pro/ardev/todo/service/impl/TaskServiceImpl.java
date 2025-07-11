package pro.ardev.todo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.ardev.todo.exception.ResourceNotFoundException;
import pro.ardev.todo.mapper.TaskMapper;
import pro.ardev.todo.model.entity.Task;
import pro.ardev.todo.model.enums.TaskStatus;
import pro.ardev.todo.model.request.CreateTaskRequest;
import pro.ardev.todo.model.request.UpdateTaskRequest;
import pro.ardev.todo.model.response.TaskResponse;
import pro.ardev.todo.repository.TaskRepository;
import pro.ardev.todo.service.TaskService;
import pro.ardev.todo.validator.TaskValidator;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.by;
import static pro.ardev.todo.model.enums.TaskStatus.TODO;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskValidator taskValidator;

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return this.taskRepository.findAll().stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest createTaskRequest) {
        Task task = this.taskMapper.toEntity(createTaskRequest);
        task.setStatus(TODO);
        Task saved = this.taskRepository.save(task);

        return this.taskMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = findTaskById(id);

        return this.taskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        boolean existsById = this.taskRepository.existsById(id);
        this.taskValidator.validate(id, existsById);
        this.taskRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest updateTaskRequest) {
        Task task = findTaskById(id);

        this.taskMapper.updateEntity(task, updateTaskRequest);
        this.taskRepository.save(task);

        return this.taskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(List<TaskStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return this.taskRepository.findAll().stream()
                    .map(this.taskMapper::toResponse)
                    .toList();
        }

        return this.taskRepository.findByStatusIn(statuses).stream()
                .map(this.taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getSortedTasks(String sortBy, String direction) {
        String sortField = this.taskValidator.validateSortField(sortBy);
        Direction sortDirection = this.taskValidator.validateSortDirection(direction);

        Sort sort = by(sortDirection, sortField);

        return this.taskRepository.findAll(sort).stream()
                .map(this.taskMapper::toResponse)
                .toList();
    }

    private Task findTaskById(Long id) {
        return this.taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Task with ID %d not found", id)));
    }
}
