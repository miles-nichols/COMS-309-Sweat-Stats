package coms309.workout.LiftSet;

import coms309.workout.Lifts.Lift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LiftSetRepository extends JpaRepository<LiftSet, Integer>
{
    LiftSet findById(int id);

    @Transactional
    void deleteById(int id);

    LiftSet findByLift(Lift lift);

    @Query(value = "SELECT ls.* " +
            "FROM Default_Workouts.completed_workout cw " +
            "JOIN Default_Workouts.lift_sets ls ON cw.id = ls.completed_workout_id " +
            "JOIN Default_Workouts.lifts l ON ls.lift_id = l.id " +
            "WHERE cw.user like :username AND cw.date BETWEEN :startDate AND :endDate AND l.title like :liftName ", nativeQuery = true)
    List<LiftSet> getLiftSetsByUserAndLiftTitleInRange(String username, String startDate, String endDate, String liftName);

    @Transactional
    void deleteByLift(Lift lift);
}