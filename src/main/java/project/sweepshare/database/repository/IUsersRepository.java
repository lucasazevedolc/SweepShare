package project.sweepshare.database.repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import project.sweepshare.database.model.UsersEntity;
import project.sweepshare.database.model.WgsEntity;

import java.util.List;
import java.util.Optional;

public interface IUsersRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByEmail(String userEmail);
    Optional<UsersEntity> findByName(String userName);
    List<UsersEntity> findByWgIdAndActiveTrueOrderByNameAsc(Long wgId);
    long countByWg(WgsEntity wg);
}
