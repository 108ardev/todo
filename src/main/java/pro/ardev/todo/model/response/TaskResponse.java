package pro.ardev.todo.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import pro.ardev.todo.model.enums.TaskStatus;

import java.time.LocalDateTime;

@Builder
public record TaskResponse(
        long id,
        String title,
        String description,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dueDate,

        TaskStatus status,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
}
