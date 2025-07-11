package pro.ardev.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.ardev.todo.model.enums.TaskStatus;
import pro.ardev.todo.model.request.CreateTaskRequest;
import pro.ardev.todo.model.request.UpdateTaskRequest;
import pro.ardev.todo.model.response.TaskResponse;
import pro.ardev.todo.service.TaskService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponse> getAllTasks() {
        return this.taskService.getAllTasks();
    }

    @GetMapping("/filter")
    public List<TaskResponse> getTasksByStatus(
            @RequestParam(required = false) List<TaskStatus> statuses) {
        return this.taskService.getTasksByStatus(statuses);
    }

    @GetMapping("/sorted")
    public List<TaskResponse> getSortedTasks(
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        return this.taskService.getSortedTasks(sortBy, direction);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody CreateTaskRequest createTaskRequest) {
        return this.taskService.createTask(createTaskRequest);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest updateTaskRequest) {
        return this.taskService.updateTask(id, updateTaskRequest);
    }

    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable("id") Long id) {
        return this.taskService.getTaskById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteTask(@PathVariable("id") Long id) {
        this.taskService.deleteTaskById(id);
    }
}
