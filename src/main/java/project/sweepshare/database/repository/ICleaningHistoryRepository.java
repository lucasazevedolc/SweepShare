package project.sweepshare.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sweepshare.database.model.CleaningHistoryEntity;

import java.util.List;

public interface ICleaningHistoryRepository extends JpaRepository<CleaningHistoryEntity, Long> {
    List<CleaningHistoryEntity> findByWgId(Long wgId);

}
