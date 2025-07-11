package pro.ardev.todo.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.ardev.todo.exception.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
class TaskValidatorTest {

    private final TaskValidator taskValidator = new TaskValidator();

    @Test
    void validate_WhenTaskNotExists_ShouldThrowException() {
        // Given
        Long nonExistingTaskId = 1L;
        boolean existsById = false;
        String expectedMessage = "Task with ID 1 not found";

        // When & Then
        assertThatThrownBy(() -> taskValidator.validate(nonExistingTaskId, existsById))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void validate_WhenTaskExists_ShouldNotThrowException() {
        // Given
        Long existingTaskId = 1L;
        boolean existsById = true;

        // When
        assertThatNoException()
                .isThrownBy(() -> taskValidator.validate(existingTaskId, existsById));
    }

    @Test
    void validateSortField_WhenInputIsNull_ShouldReturnDefault() {
        // When
        String result = taskValidator.validateSortField(null);

        // Then
        assertThat(result).isEqualTo("dueDate");
    }

    @Test
    void validateSortField_WhenInputIsValid_ShouldReturnCorrectField() {
        // When & Then
        assertThat(taskValidator.validateSortField("dueDate")).isEqualTo("dueDate");
        assertThat(taskValidator.validateSortField("status")).isEqualTo("status");
    }

    @Test
    void validateSortField_WhenInputIsInvalid_ShouldReturnDefault() {
        // When
        String result = taskValidator.validateSortField("invalidField");

        // Then
        assertThat(result).isEqualTo("dueDate");
    }

    @Test
    void validateSortDirection_WhenInputIsNull_ShouldReturnDefault() {
        // When
        Direction result = taskValidator.validateSortDirection(null);

        // Then
        assertThat(result).isEqualTo(ASC);
    }

    @Test
    void validateSortDirection_WhenInputIsDesc_ShouldReturnDESC() {
        // When & Then
        assertThat(taskValidator.validateSortDirection("DESC")).isEqualTo(DESC);
        assertThat(taskValidator.validateSortDirection("desc")).isEqualTo(DESC);
    }

    @Test
    void validateSortDirection_WhenInputIsNotDesc_ShouldReturnASC() {
        // When & Then
        assertThat(taskValidator.validateSortDirection("ASC")).isEqualTo(ASC);
        assertThat(taskValidator.validateSortDirection("asc")).isEqualTo(ASC);
        assertThat(taskValidator.validateSortDirection("invalid")).isEqualTo(ASC);
    }
}
