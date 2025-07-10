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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.ardev.todo.model.dto.CreateTaskRequestDto;
import pro.ardev.todo.model.dto.TaskResponseDto;
import pro.ardev.todo.model.dto.UpdateTaskRequestDto;
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
    public List<TaskResponseDto> getAllTasks() {
        return this.taskService.getAllTasks();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public TaskResponseDto createTask(@Valid @RequestBody CreateTaskRequestDto createTaskRequestDto) {
        return this.taskService.createTask(createTaskRequestDto);
    }

    @PutMapping("/{id}")
    public TaskResponseDto updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequestDto updateTaskRequestDto) {
        return this.taskService.updateTask(id, updateTaskRequestDto);
    }

    @GetMapping("/{id}")
    public TaskResponseDto getTask(@PathVariable("id") Long id) {
        return this.taskService.getTaskById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteTask(@PathVariable("id") Long id) {
        this.taskService.deleteTaskById(id);
    }
}
