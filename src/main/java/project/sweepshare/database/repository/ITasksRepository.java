package project.sweepshare.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sweepshare.database.model.TasksEntity;

import java.util.List;

public interface ITasksRepository extends JpaRepository<TasksEntity, Long> {
    List<TasksEntity> findByRoomId(Long roomId);

    boolean existsByNameIgnoreCaseAndRoomId(String name, Long roomId);

    List<TasksEntity> findByRoomWgId(Long wgId);
}
