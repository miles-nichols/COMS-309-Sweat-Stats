package coms309.workout.Workout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long>
{
    Workout findById(int id);

    @Transactional
    void deleteById(int id);

    Workout findByName(String name);

    @Transactional
    void deleteByName(String name);
}