package project.sweepshare.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sweepshare.database.model.RoomsAssignmentsEntity;

import java.util.List;
import java.util.Optional;

public interface IRoomsAssignmentsRepository extends JpaRepository<RoomsAssignmentsEntity, Long> {
    List<RoomsAssignmentsEntity> findByRoomWgId(Long roomWgId);

    boolean existsByRoomId(Long id);

    List<RoomsAssignmentsEntity> findByIsCompletedFalse();
}
