package pro.ardev.todo.model.response;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int status,
        String message,
        long timestamp
) {
}
