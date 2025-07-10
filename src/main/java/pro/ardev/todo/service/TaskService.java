package pro.ardev.todo.service;

import pro.ardev.todo.model.dto.CreateTaskRequestDto;
import pro.ardev.todo.model.dto.TaskResponseDto;
import pro.ardev.todo.model.dto.UpdateTaskRequestDto;

import java.util.List;

public interface TaskService {

    List<TaskResponseDto> getAllTasks();

    TaskResponseDto createTask(CreateTaskRequestDto createTaskRequestDto);

    TaskResponseDto getTaskById(Long id);

    void deleteTaskById(Long id);

    TaskResponseDto updateTask(Long id, UpdateTaskRequestDto updateTaskRequestDto);
}
