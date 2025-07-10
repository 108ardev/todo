package pro.ardev.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.ardev.todo.model.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
