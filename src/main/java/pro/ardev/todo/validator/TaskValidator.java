package pro.ardev.todo.validator;

import org.springframework.stereotype.Component;
import pro.ardev.todo.exception.ResourceNotFoundException;

import static java.util.Arrays.asList;
import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Component
public class TaskValidator {

    public void validate(Long id, boolean existsById) {
        if (!existsById) {
            throw new ResourceNotFoundException(
                    String.format("Task with ID %d not found", id));
        }
    }

    public String validateSortField(String sortBy) {
        if (sortBy == null) {
            return "dueDate";
        }

        return asList("dueDate", "status").contains(sortBy)
                ? sortBy
                : "dueDate";
    }

    public Direction validateSortDirection(String direction) {
        if (direction == null) {
            return ASC;
        }

        return "DESC".equalsIgnoreCase(direction)
                ? DESC
                : ASC;
    }
}
