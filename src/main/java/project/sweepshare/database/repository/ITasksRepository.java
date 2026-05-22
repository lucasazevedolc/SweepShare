package project.sweepshare.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sweepshare.database.model.TasksEntity;

import java.util.List;
import java.util.Optional;

public interface ITasksRepository extends JpaRepository<TasksEntity, Long> {
    List<TasksEntity> findByRoomId(Long roomId);

    boolean existsByNameIgnoreCaseAndRoomId(String name, Long roomId);
}
