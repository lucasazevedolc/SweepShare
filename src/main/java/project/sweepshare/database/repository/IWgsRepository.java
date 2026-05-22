package project.sweepshare.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.model.WgsEntity;

import java.util.Optional;

public interface IWgsRepository extends JpaRepository<WgsEntity, Long> {
}
