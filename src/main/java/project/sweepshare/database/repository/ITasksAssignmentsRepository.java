package project.sweepshare.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sweepshare.database.model.TasksAssignmentsEntity;

import java.util.List;
import java.util.Optional;

public interface ITasksAssignmentsRepository extends JpaRepository<TasksAssignmentsEntity, Long> {
    List<TasksAssignmentsEntity> findByTaskRoomWgId(Long wgId);

    boolean existsByTaskId(Long taskId);

    Optional <TasksAssignmentsEntity> findByTaskId(Long id);
}
