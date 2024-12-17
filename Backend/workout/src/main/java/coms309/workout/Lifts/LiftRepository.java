package coms309.workout.Lifts;

import coms309.workout.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

import java.util.List;



public interface LiftRepository extends JpaRepository<Lift, Long> {
    Lift findById(int id);

    @Transactional
    void deleteById(int id);

    @Transactional
    void deleteByTitle(String title);

    Lift findByTitle(String title);

    public List<Lift> findByTitleContains(String title);

    List<Lift> findByMuscleGroup(String muscleGroup);

    List<Lift> findByEquipment(String equipment);

    List<Lift> findByType(String type);
}
