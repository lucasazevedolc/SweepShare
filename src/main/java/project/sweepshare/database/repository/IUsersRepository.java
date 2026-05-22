package project.sweepshare.database.repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.model.WgsEntity;

import java.util.Optional;

public interface IUsersRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByEmail(String userEmail);
    Optional<UsersEntity> findByName(String userName);
    long countByWg(WgsEntity wg);
}
