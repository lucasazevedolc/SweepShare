package project.sweepshare.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sweepshare.database.model.WgsEntity;
import project.sweepshare.enums.CleaningStyle;

import java.util.List;
import java.util.Optional;

public interface IWgsRepository extends JpaRepository<WgsEntity, Long> {
    List<WgsEntity> findByCleaningStyle(Integer cleaningStyle);
}
