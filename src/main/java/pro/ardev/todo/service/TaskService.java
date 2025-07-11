package pro.ardev.todo.service;

import pro.ardev.todo.model.enums.TaskStatus;
import pro.ardev.todo.model.request.CreateTaskRequest;
import pro.ardev.todo.model.request.UpdateTaskRequest;
import pro.ardev.todo.model.response.TaskResponse;

import java.util.List;

public interface TaskService {

    List<TaskResponse> getAllTasks();

    TaskResponse createTask(CreateTaskRequest createTaskRequest);

    TaskResponse getTaskById(Long id);

    void deleteTaskById(Long id);

    TaskResponse updateTask(Long id, UpdateTaskRequest updateTaskRequest);

    List<TaskResponse> getTasksByStatus(List<TaskStatus> statuses);

    List<TaskResponse> getSortedTasks(String sortBy, String direction);
}
