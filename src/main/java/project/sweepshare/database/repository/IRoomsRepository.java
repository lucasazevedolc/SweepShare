package project.sweepshare.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sweepshare.database.model.RoomsEntity;
import project.sweepshare.database.model.WgsEntity;

import java.util.List;
import java.util.Optional;

public interface IRoomsRepository extends JpaRepository<RoomsEntity, Long> {
    List<RoomsEntity> findByWgId(Long wgId);

    boolean existsByNameIgnoreCaseAndWgId(String name, Long wgId);
}
