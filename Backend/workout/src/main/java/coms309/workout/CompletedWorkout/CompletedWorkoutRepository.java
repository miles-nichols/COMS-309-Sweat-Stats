package coms309.workout.CompletedWorkout;

import coms309.workout.LiftSet.LiftSet;
import coms309.workout.Lifts.Lift;
import coms309.workout.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface CompletedWorkoutRepository extends JpaRepository<CompletedWorkout, Long>
{
    CompletedWorkout findById(int id);

    @Transactional
    void deleteById(int id);

    CompletedWorkout findByName(String name);

    CompletedWorkout findByNameAndUser(String name, User user);

    CompletedWorkout findByNameAndUserAndDate(String name, User user, LocalDate date);

    List<CompletedWorkout> findByUserUsernameOrderByDateAsc(String username);

    List<CompletedWorkout> findByUserUsernameAndDate(String username, LocalDate date);


    @Query(value = "SELECT * FROM completed_workout cw WHERE cw.user like ?1 AND cw.date BETWEEN ?2 AND ?3", nativeQuery = true)
    List<CompletedWorkout> getCompletedWorkoutsByUserInRange(String username, String startDate, String endDate);

    @Query(value = "SELECT ls.* " +
            "FROM Default_Workouts.completed_workout cw " +
            "JOIN Default_Workouts.lift_sets ls ON cw.id = ls.completed_workout_id " +
            "JOIN Default_Workouts.lifts l ON ls.lift_id = l.id " +
            "WHERE cw.user like :username AND cw.date BETWEEN :startDate AND :endDate " +
            "GROUP BY l.title", nativeQuery = true)
    List<LiftSet> getLiftsByUserInRange(String username, String startDate, String endDate);

    @Query(value = "SELECT ls.* " +
            "FROM Default_Workouts.completed_workout cw " +
            "JOIN Default_Workouts.lift_sets ls ON cw.id = ls.completed_workout_id " +
            "JOIN Default_Workouts.lifts l ON ls.lift_id = l.id " +
            "WHERE cw.user like :username AND cw.date BETWEEN :startDate AND :endDate AND l.title like :liftName ", nativeQuery = true)
    List<LiftSet> getLiftSetsByUserAndLiftTitleInRange(String username, String startDate, String endDate, String liftName);

    @Query(value = "SELECT l.title " +
            "FROM Default_Workouts.completed_workout cw " +
            "JOIN Default_Workouts.lift_sets ls ON cw.id = ls.completed_workout_id " +
            "JOIN Default_Workouts.lifts l ON ls.lift_id = l.id " +
            "where cw.user like :username " +
            "GROUP BY l.title", nativeQuery = true)
    List<String> getLiftsByUser(String username);


    @Query(value = "SELECT l.title " +
            "FROM Default_Workouts.completed_workout cw " +
            "JOIN Default_Workouts.lift_sets ls ON cw.id = ls.completed_workout_id " +
            "JOIN Default_Workouts.lifts l ON ls.lift_id = l.id " +
            "JOIN Default_Workouts.lift_set_sets s ON s.lift_set_id = ls.id " +
            "WHERE cw.user = :username "+
            "AND ("+
                    "cw.date BETWEEN :startDate1 AND :endDate1 "+
            "OR cw.date BETWEEN :startDate2 AND :endDate2) "+
    "GROUP BY l.title " +
        "HAVING "+
    "SUM(CASE WHEN cw.date BETWEEN :startDate1 AND :endDate1 THEN 1 ELSE 0 END) > 0 " +
    "AND SUM(CASE WHEN cw.date BETWEEN :startDate2 AND :endDate2 THEN 1 ELSE 0 END) > 0", nativeQuery = true)
    List<String> getLiftsByUserInTwoRange(String username, String startDate1, String endDate1, String startDate2, String endDate2);

    @Query(value = "SELECT tab.muscle_group FROM(SELECT l.* " +
            "FROM Default_Workouts.completed_workout cw " +
            "JOIN Default_Workouts.lift_sets ls ON cw.id = ls.completed_workout_id " +
            "JOIN Default_Workouts.lifts l ON ls.lift_id = l.id "+
            "WHERE cw.user = :username "+
            "AND ("+
            "cw.date BETWEEN :startDate1 AND :endDate1 "+
            "OR cw.date BETWEEN :startDate2 AND :endDate2 ) "+
            "GROUP BY l.title " +
            "HAVING "+
            "SUM(CASE WHEN cw.date BETWEEN :startDate1 AND :endDate1 THEN 1 ELSE 0 END) > 0 " +
            "AND SUM(CASE WHEN cw.date BETWEEN :startDate2 AND :endDate2 THEN 1 ELSE 0 END) > 0) as tab " +
            "GROUP BY tab.muscle_group", nativeQuery = true)
    List<String> getMuscleGroupsByUserInTwoRange(String username, String startDate1, String endDate1, String startDate2, String endDate2);

    @Query(value = "SELECT MAX(s.weight) " +
            "FROM Default_Workouts.completed_workout cw " +
            "JOIN Default_Workouts.lift_sets ls ON cw.id = ls.completed_workout_id " +
            "JOIN Default_Workouts.lifts l ON ls.lift_id = l.id " +
            "JOIN Default_Workouts.lift_set_sets s ON s.lift_set_id = ls.id "+
            "WHERE cw.user = :username " +
            "AND lower(l.title) like lower(CONCAT('%',:key,'%'))"
            , nativeQuery = true)
    Double getMaxByUserAndContainsKey(String username, String key);

    @Query(value = "SELECT l.title " +
            "FROM Default_Workouts.completed_workout cw " +
            "JOIN Default_Workouts.lift_sets ls ON cw.id = ls.completed_workout_id " +
            "JOIN Default_Workouts.lifts l ON ls.lift_id = l.id "+
            "WHERE cw.user = :username " +
            "AND l.muscle_group like :muscleGroup "+
            "AND ("+
            "cw.date BETWEEN :startDate1 AND :endDate1 "+
            "OR cw.date BETWEEN :startDate2 AND :endDate2 ) "+
            "GROUP BY l.title " +
            "HAVING "+
            "SUM(CASE WHEN cw.date BETWEEN :startDate1 AND :endDate1 THEN 1 ELSE 0 END) > 0 " +
            "AND SUM(CASE WHEN cw.date BETWEEN :startDate2 AND :endDate2 THEN 1 ELSE 0 END) > 0", nativeQuery = true)
    List<String> getLiftsByUserInTwoRangeByMuscleGroup(String username, String startDate1, String endDate1, String startDate2, String endDate2, String muscleGroup);
}