package coms309.workout.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

    List<User> findByUsernameStartingWith(String username);

    User findByEmail(String email);

    User findByUsername(String username);

    @Transactional
    void deleteByUsername(String username);

    User findByEmailAndUsername(String email, String username);

}