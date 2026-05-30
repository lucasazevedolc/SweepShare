package project.sweepshare.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.sweepshare.database.model.TasksEntity;

import java.util.List;

public interface ITasksRepository extends JpaRepository<TasksEntity, Long> {
    List<TasksEntity> findByRoomId(Long roomId);

    boolean existsByNameIgnoreCaseAndRoomId(String name, Long roomId);

    List<TasksEntity> findByRoomWgId(Long wgId);

    @Query("SELECT t FROM TasksEntity t JOIN FETCH t.room r WHERE r.wg.id = :wgId")
    List<TasksEntity> findTasksWithRoomsByWgId(@Param("wgId") Long wgId);
}
