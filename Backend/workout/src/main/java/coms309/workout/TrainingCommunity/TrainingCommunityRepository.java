package coms309.workout.TrainingCommunity;

import coms309.workout.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingCommunityRepository extends JpaRepository<TrainingCommunity, Long>
{
    List<TrainingCommunity> findAll();

    List<TrainingCommunity> findByTrainer(User trainer);

    List<TrainingCommunity> findByTitle(String title);

    TrainingCommunity findByTrainerAndTitle(User trainer, String title);
}
