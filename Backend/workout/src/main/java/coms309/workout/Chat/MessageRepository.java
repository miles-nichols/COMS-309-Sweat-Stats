package coms309.workout.Chat;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long>
{
    List<Message> findBySenderOrRecipientOrderBySentAsc(String sender, String recipient);

    List<Message> findBySenderOrRecipientStartingWithOrderBySentAsc(String username, String startingWith);

    @Transactional
    void deleteAll();

}