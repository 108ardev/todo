package pro.ardev.todo.exception;

public record ErrorResponse(
        int status,
        String message,
        long timestamp
) {
}
