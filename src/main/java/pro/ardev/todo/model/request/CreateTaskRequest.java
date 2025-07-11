package pro.ardev.todo.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateTaskRequest(
        @NotBlank(message = "Title is mandatory")
        @Size(max = 255, message = "Title must be less than 255 characters")
        String title,

        @Size(max = 2000, message = "Description must be less than 2000 characters")
        String description,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @FutureOrPresent(message = "Due date must be in present or future")
        LocalDateTime dueDate
) {
}
