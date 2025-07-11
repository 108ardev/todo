package pro.ardev.todo.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import pro.ardev.todo.exception.ResourceNotFoundException;
import pro.ardev.todo.mapper.TaskMapper;
import pro.ardev.todo.model.entity.Task;
import pro.ardev.todo.model.enums.TaskStatus;
import pro.ardev.todo.model.request.CreateTaskRequest;
import pro.ardev.todo.model.request.UpdateTaskRequest;
import pro.ardev.todo.model.response.TaskResponse;
import pro.ardev.todo.repository.TaskRepository;
import pro.ardev.todo.validator.TaskValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static pro.ardev.todo.model.enums.TaskStatus.DONE;
import static pro.ardev.todo.model.enums.TaskStatus.IN_PROGRESS;
import static pro.ardev.todo.model.enums.TaskStatus.TODO;
import static pro.ardev.todo.model.response.TaskResponse.builder;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskValidator taskValidator;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void getAllTasks_WhenTasksExist_ShouldReturnListOfTasks() {
        // Given
        Task task1 = Task.builder()
                .id(1L)
                .title("Task 1")
                .description("Description 1")
                .dueDate(now().plusDays(1))
                .status(TODO)
                .build();

        Task task2 = Task.builder()
                .id(2L)
                .title("Task 2")
                .description("Description 2")
                .dueDate(now().plusDays(2))
                .status(IN_PROGRESS)
                .build();

        TaskResponse response1 = builder()
                .id(1L)
                .title("Task 1")
                .description("Description 1")
                .build();

        TaskResponse response2 = builder()
                .id(2L)
                .title("Task 2")
                .description("Description 2")
                .build();

        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));
        when(taskMapper.toResponse(task1)).thenReturn(response1);
        when(taskMapper.toResponse(task2)).thenReturn(response2);

        // When
        List<TaskResponse> result = taskService.getAllTasks();

        // Then
        assertThat(result)
                .hasSize(2)
                .containsExactly(response1, response2);
    }

    @Test
    void getAllTasks_WhenNoTasksExist_ShouldReturnEmptyList() {
        // Given
        when(taskRepository.findAll()).thenReturn(List.of());

        // When
        List<TaskResponse> result = taskService.getAllTasks();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void createTask_WithValidRequest_ShouldReturnCreatedTask() {
        // Given
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("New Task")
                .description("Task Description")
                .dueDate(now().plusDays(1))
                .build();

        Task unsavedTask = Task.builder()
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .build();

        Task savedTask = Task.builder()
                .id(1L)
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .status(TODO)
                .createdAt(now())
                .updatedAt(now())
                .build();

        TaskResponse expectedResponse = builder()
                .id(1L)
                .title(savedTask.getTitle())
                .description(savedTask.getDescription())
                .dueDate(savedTask.getDueDate())
                .status(savedTask.getStatus())
                .createdAt(savedTask.getCreatedAt())
                .updatedAt(savedTask.getUpdatedAt())
                .build();

        when(taskMapper.toEntity(request)).thenReturn(unsavedTask);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.toResponse(savedTask)).thenReturn(expectedResponse);

        // When
        TaskResponse result = taskService.createTask(request);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResponse);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.status()).isEqualTo(TODO);

        verify(taskMapper).toEntity(request);
        verify(taskRepository).save(unsavedTask);
        verify(taskMapper).toResponse(savedTask);
    }

    @Test
    void createTask_ShouldSetDefaultStatusToTODO() {
        // Given
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("Task without status")
                .build();

        Task unsavedTask = Task.builder()
                .title(request.title())
                .build();

        Task savedTask = Task.builder()
                .id(1L)
                .title(request.title())
                .status(TODO) // Проверяем, что статус установлен
                .build();

        when(taskMapper.toEntity(request)).thenReturn(unsavedTask);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.toResponse(savedTask)).thenReturn(
                builder().id(1L).title("Task without status").status(TODO).build()
        );

        // When
        TaskResponse result = taskService.createTask(request);

        // Then
        assertThat(result.status()).isEqualTo(TODO);
    }

    @Test
    void createTask_ShouldMapAllFieldsCorrectly() {
        // Given
        LocalDateTime dueDate = now().plusDays(1);
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("Complete project")
                .description("Finish all modules")
                .dueDate(dueDate)
                .build();

        Task unsavedTask = Task.builder()
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .build();

        Task savedTask = Task.builder()
                .id(1L)
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .status(TODO)
                .build();

        when(taskMapper.toEntity(request)).thenReturn(unsavedTask);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.toResponse(savedTask)).thenReturn(
                builder()
                        .id(1L)
                        .title(request.title())
                        .description(request.description())
                        .dueDate(request.dueDate())
                        .status(TODO)
                        .build()
        );

        // When
        TaskResponse result = taskService.createTask(request);

        // Then
        assertThat(result.title()).isEqualTo(request.title());
        assertThat(result.description()).isEqualTo(request.description());
        assertThat(result.dueDate()).isEqualTo(request.dueDate());
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTaskResponse() {
        // Given
        Long taskId = 1L;
        Task task = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .dueDate(now().plusDays(1))
                .status(TODO)
                .createdAt(now())
                .updatedAt(now())
                .build();

        TaskResponse expectedResponse = builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .dueDate(task.getDueDate())
                .status(TODO)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(expectedResponse);

        // When
        TaskResponse result = taskService.getTaskById(taskId);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResponse);

        verify(taskRepository).findById(taskId);
        verify(taskMapper).toResponse(task);
    }

    @Test
    void getTaskById_WhenTaskNotExists_ShouldThrowException() {
        // Given
        Long nonExistentTaskId = 999L;
        when(taskRepository.findById(nonExistentTaskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.getTaskById(nonExistentTaskId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task with ID " + nonExistentTaskId + " not found");

        verify(taskRepository).findById(nonExistentTaskId);
    }

    @Test
    void getTaskById_ShouldCallRepositoryWithCorrectId() {
        // Given
        Long taskId = 1L;
        Task task = Task.builder().id(taskId).build();
        TaskResponse response = builder().id(taskId).build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        // When
        taskService.getTaskById(taskId);

        // Then
        verify(taskRepository).findById(taskId);
    }

    @Test
    void getTaskById_ShouldMapEntityToResponseCorrectly() {
        // Given
        Long taskId = 1L;
        Task task = Task.builder()
                .id(taskId)
                .title("Mapping Test")
                .build();

        TaskResponse expectedResponse = builder()
                .id(taskId)
                .title("Mapping Test")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(expectedResponse);

        // When
        TaskResponse result = taskService.getTaskById(taskId);

        // Then
        assertThat(result.title()).isEqualTo(task.getTitle());
        verify(taskMapper).toResponse(task);
    }

    @Test
    void deleteTaskById_WhenTaskExists_ShouldDeleteSuccessfully() {
        // Given
        Long existingTaskId = 1L;
        when(taskRepository.existsById(existingTaskId)).thenReturn(true);

        // When
        taskService.deleteTaskById(existingTaskId);

        // Then
        verify(taskValidator).validate(existingTaskId, true);
        verify(taskRepository).deleteById(existingTaskId);
    }

    @Test
    void deleteTaskById_WhenTaskNotExists_ShouldThrowException() {
        // Given
        Long nonExistingTaskId = 999L;
        when(taskRepository.existsById(nonExistingTaskId)).thenReturn(false);

        // Настраиваем валидатор на выброс исключения
        doThrow(new ResourceNotFoundException("Task with ID " + nonExistingTaskId + " not found"))
                .when(taskValidator).validate(nonExistingTaskId, false);

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTaskById(nonExistingTaskId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task with ID " + nonExistingTaskId + " not found");

        verify(taskRepository).existsById(nonExistingTaskId);
        verify(taskValidator).validate(nonExistingTaskId, false);
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void deleteTaskById_ShouldCallExistsBeforeDelete() {
        // Given
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(true);

        // When
        taskService.deleteTaskById(taskId);

        // Then
        verify(taskRepository).existsById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void deleteTaskById_ShouldCallValidatorWithCorrectParameters() {
        // Given
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(true);

        // When
        taskService.deleteTaskById(taskId);

        // Then
        verify(taskValidator).validate(taskId, true);
    }

    @Test
    void deleteTaskById_ShouldNotCallDeleteWhenValidationFails() {
        // Given
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(false);
        doThrow(new ResourceNotFoundException("Task with ID " + taskId + " not found"))
                .when(taskValidator).validate(taskId, false);

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTaskById(taskId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void updateTask_WhenTaskNotExists_ShouldThrowException() {
        // Given
        Long nonExistingTaskId = 999L;
        UpdateTaskRequest request = UpdateTaskRequest.builder().build();
        when(taskRepository.findById(nonExistingTaskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.updateTask(nonExistingTaskId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task with ID " + nonExistingTaskId + " not found");

        verify(taskRepository, never()).save(any());
        verify(taskMapper, never()).updateEntity(any(), any());
    }

    @Test
    void updateTask_ShouldCallMapperWithCorrectParameters() {
        // Given
        Long taskId = 1L;
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("New Title")
                .build();

        Task existingTask = Task.builder().id(taskId).build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any())).thenReturn(existingTask);
        when(taskMapper.toResponse(any())).thenReturn(builder().id(taskId).build());

        // When
        taskService.updateTask(taskId, request);

        // Then
        verify(taskMapper).updateEntity(existingTask, request);
    }

    @Test
    void updateTask_ShouldSaveUpdatedEntity() {
        // Given
        Long taskId = 1L;
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("Updated")
                .build();

        Task existingTask = Task.builder().id(taskId).build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskMapper.toResponse(any())).thenReturn(builder().id(taskId).build());

        // When
        taskService.updateTask(taskId, request);

        // Then
        verify(taskRepository).save(existingTask);
    }

    @Test
    void updateTask_ShouldReturnCorrectResponse() {
        // Given
        Long taskId = 1L;
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("Correct Response Test")
                .build();

        Task existingTask = Task.builder().id(taskId).build();
        TaskResponse expectedResponse = builder()
                .id(taskId)
                .title("Correct Response Test")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any())).thenReturn(existingTask);
        when(taskMapper.toResponse(existingTask)).thenReturn(expectedResponse);

        // When
        TaskResponse result = taskService.updateTask(taskId, request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getTasksByStatus_WhenStatusesNull_ShouldReturnAllTasks() {
        // Given
        Task task1 = Task.builder()
                .id(1L)
                .title("Task 1")
                .status(TODO)
                .build();

        Task task2 = Task.builder()
                .id(2L)
                .title("Task 2")
                .status(IN_PROGRESS)
                .build();

        TaskResponse response1 = builder().id(1L).title("Task 1").build();
        TaskResponse response2 = builder().id(2L).title("Task 2").build();

        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));
        when(taskMapper.toResponse(task1)).thenReturn(response1);
        when(taskMapper.toResponse(task2)).thenReturn(response2);

        // When
        List<TaskResponse> result = taskService.getTasksByStatus(null);

        // Then
        assertThat(result)
                .hasSize(2)
                .containsExactly(response1, response2);

        verify(taskRepository).findAll();
        verify(taskRepository, never()).findByStatusIn(any());
    }

    @Test
    void getTasksByStatus_WhenStatusesEmpty_ShouldReturnAllTasks() {
        // Given
        Task task = Task.builder().id(1L).title("Task 1").build();
        TaskResponse response = builder().id(1L).title("Task 1").build();

        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        // When
        List<TaskResponse> result = taskService.getTasksByStatus(List.of());

        // Then
        assertThat(result).containsExactly(response);
        verify(taskRepository).findAll();
    }

    @Test
    void getTasksByStatus_WithSingleStatus_ShouldReturnFilteredTasks() {
        // Given
        TaskStatus status = TODO;
        Task task = Task.builder()
                .id(1L)
                .title("Task 1")
                .status(status)
                .build();

        TaskResponse response = builder()
                .id(1L)
                .title("Task 1")
                .status(status)
                .build();

        when(taskRepository.findByStatusIn(List.of(status))).thenReturn(List.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        // When
        List<TaskResponse> result = taskService.getTasksByStatus(List.of(status));

        // Then
        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(TaskResponse::status)
                .isEqualTo(status);

        verify(taskRepository).findByStatusIn(List.of(status));
        verify(taskRepository, never()).findAll();
    }

    @Test
    void getTasksByStatus_WithMultipleStatuses_ShouldReturnFilteredTasks() {
        // Given
        List<TaskStatus> statuses = List.of(TODO, IN_PROGRESS);

        Task task1 = Task.builder().id(1L).status(TODO).build();
        Task task2 = Task.builder().id(2L).status(IN_PROGRESS).build();

        TaskResponse response1 = builder().id(1L).status(TODO).build();
        TaskResponse response2 = builder().id(2L).status(IN_PROGRESS).build();

        when(taskRepository.findByStatusIn(statuses)).thenReturn(List.of(task1, task2));
        when(taskMapper.toResponse(task1)).thenReturn(response1);
        when(taskMapper.toResponse(task2)).thenReturn(response2);

        // When
        List<TaskResponse> result = taskService.getTasksByStatus(statuses);

        // Then
        assertThat(result)
                .hasSize(2)
                .extracting(TaskResponse::status)
                .containsExactlyInAnyOrder(TODO, IN_PROGRESS);
    }

    @Test
    void getTasksByStatus_ShouldMapAllTasksToResponse() {
        // Given
        Task task = Task.builder().id(1L).build();
        TaskResponse response = builder().id(1L).build();

        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        // When
        List<TaskResponse> result = taskService.getTasksByStatus(null);

        // Then
        verify(taskMapper).toResponse(task);
        assertThat(result).containsExactly(response);
    }

    @Test
    void getTasksByStatus_ShouldReturnEmptyListWhenNoTasksFound() {
        // Given
        when(taskRepository.findByStatusIn(List.of(DONE))).thenReturn(List.of());

        // When
        List<TaskResponse> result = taskService.getTasksByStatus(List.of(DONE));

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getSortedTasks_ShouldUseDefaultSortWhenParamsNull() {
        // Given
        String defaultSortField = "dueDate";
        Direction defaultDirection = ASC;

        Task task = Task.builder().id(1L).build();
        TaskResponse response = builder().id(1L).build();

        when(taskValidator.validateSortField(null)).thenReturn(defaultSortField);
        when(taskValidator.validateSortDirection(null)).thenReturn(defaultDirection);
        when(taskRepository.findAll(by(defaultDirection, defaultSortField))).thenReturn(List.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        // When
        List<TaskResponse> result = taskService.getSortedTasks(null, null);

        // Then
        assertThat(result).containsExactly(response);
        verify(taskValidator).validateSortField(null);
        verify(taskValidator).validateSortDirection(null);
    }

    @Test
    void getSortedTasks_ShouldUseProvidedSortParameters() {
        // Given
        String sortBy = "status";
        String direction = "DESC";
        Direction sortDirection = DESC;

        Task task = Task.builder().id(1L).build();
        TaskResponse response = builder().id(1L).build();

        when(taskValidator.validateSortField(sortBy)).thenReturn(sortBy);
        when(taskValidator.validateSortDirection(direction)).thenReturn(sortDirection);
        when(taskRepository.findAll(by(sortDirection, sortBy))).thenReturn(List.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        // When
        List<TaskResponse> result = taskService.getSortedTasks(sortBy, direction);

        // Then
        assertThat(result).containsExactly(response);
        verify(taskRepository).findAll(by(sortDirection, sortBy));
    }

    @Test
    void getSortedTasks_ShouldReturnEmptyListWhenNoTasks() {
        // Given
        when(taskValidator.validateSortField(any())).thenReturn("dueDate");
        when(taskValidator.validateSortDirection(any())).thenReturn(ASC);
        when(taskRepository.findAll(any(Sort.class))).thenReturn(List.of());

        // When
        List<TaskResponse> result = taskService.getSortedTasks("dueDate", "ASC");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getSortedTasks_ShouldMapAllTasksToResponse() {
        // Given
        Task task1 = Task.builder().id(1L).build();
        Task task2 = Task.builder().id(2L).build();

        TaskResponse response1 = builder().id(1L).build();
        TaskResponse response2 = builder().id(2L).build();

        when(taskValidator.validateSortField(any())).thenReturn("dueDate");
        when(taskValidator.validateSortDirection(any())).thenReturn(ASC);
        when(taskRepository.findAll(any(Sort.class))).thenReturn(List.of(task1, task2));
        when(taskMapper.toResponse(task1)).thenReturn(response1);
        when(taskMapper.toResponse(task2)).thenReturn(response2);

        // When
        List<TaskResponse> result = taskService.getSortedTasks("dueDate", "ASC");

        // Then
        assertThat(result).containsExactly(response1, response2);
        verify(taskMapper, times(2)).toResponse(any());
    }
}
