package pro.ardev.todo.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.ardev.todo.model.entity.Task;
import pro.ardev.todo.model.enums.TaskStatus;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatusIn(List<TaskStatus> statuses);

    List<Task> findAll(Sort sort);
}
