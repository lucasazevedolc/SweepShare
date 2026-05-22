package project.sweepshare.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sweepshare.database.model.TasksAssignmentsEntity;

import java.util.List;

public interface ITasksAssignmentsRepository extends JpaRepository<TasksAssignmentsEntity, Long> {
    List<TasksAssignmentsEntity> findByTaskRoomWgId(Long wgId);

    boolean existsByTaskId(Long taskId);
}
