package coms309.workout;

import static java.lang.Double.NaN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import coms309.workout.CompletedWorkout.CompletedWorkout;
import coms309.workout.CompletedWorkout.CompletedWorkoutRepository;
import coms309.workout.CompletedWorkout.CompletedWorkoutService;
import coms309.workout.LiftSet.LiftSet;
import coms309.workout.Lifts.Lift;
import coms309.workout.Lifts.LiftRepository;
import coms309.workout.Set.Set;
import coms309.workout.Users.User;
import coms309.workout.Users.UserRepository;
import coms309.workout.Users.UserService;
import coms309.workout.Workout.Workout;
import coms309.workout.Workout.WorkoutRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProgressTrackingTests {

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private CompletedWorkoutService completedWorkoutService;

    @Mock
    CompletedWorkoutRepository completedWorkoutRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LiftRepository liftRepository;

    @Mock
    private WorkoutRepository workoutRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getProgressTest() {
        Lift bench = new Lift("Bench Press", "description", "type", "MuscleGroup", "Equipment", "level");
        when(liftRepository.findByTitle("Bench Press")).thenReturn(bench);

        User user = new User("username", "pass", "user@email.com");
        when(userRepository.findByUsername("username")).thenReturn(user);

        Workout w = new Workout("template", "this is used as a template");
        w.addLift(bench);
        when(workoutRepository.save(w)).thenReturn(w);

        CompletedWorkout workout1 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout1", user, w, LocalDate.parse("2024-01-20"));
        CompletedWorkout workout2 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout2", user, w, LocalDate.parse("2024-01-21"));
        CompletedWorkout workout3 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout3", user, w, LocalDate.parse("2024-02-20"));
        CompletedWorkout workout4 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout4", user, w, LocalDate.parse("2024-02-21"));

        when(completedWorkoutRepository.findByName("Workout1")).thenReturn(workout1);
        when(completedWorkoutRepository.findByName("Workout2")).thenReturn(workout2);
        when(completedWorkoutRepository.findByName("Workout3")).thenReturn(workout3);
        when(completedWorkoutRepository.findByName("Workout4")).thenReturn(workout4);

        Set set1 = new Set(8, 125);
        Set set2 = new Set(7, 130);
        Set set3 = new Set(8, 130);
        Set set4 = new Set(9, 130);
        completedWorkoutService.addSetToLiftSet("Workout1", "Bench Press", set1, "username");
        completedWorkoutService.addSetToLiftSet("Workout2", "Bench Press", set2, "username");
        completedWorkoutService.addSetToLiftSet("Workout3", "Bench Press", set3, "username");
        completedWorkoutService.addSetToLiftSet("Workout4", "Bench Press", set4, "username");

        Double result = (Double) userService.compareUserLifts(
                "2024-01-19", "2024-01-22",
                "2024-02-19", "2024-02-22",
                "Bench Press", "username").get("comparison");

        // Validate the result
        assertEquals(.05, result, 0.01);
    }

    @Test
    public void getProgressMultipleSetsTest() {
        Lift bench = new Lift("Bench Press", "description", "type", "MuscleGroup", "Equipment", "level");
        when(liftRepository.findByTitle("Bench Press")).thenReturn(bench);

        User user = new User("username", "pass", "user@email.com");
        when(userRepository.findByUsername("username")).thenReturn(user);

        Workout w = new Workout("template", "this is used as a template");
        w.addLift(bench);
        when(workoutRepository.save(w)).thenReturn(w);

        CompletedWorkout workout1 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout1", user, w, LocalDate.parse("2024-01-20"));
        CompletedWorkout workout2 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout2", user, w, LocalDate.parse("2024-01-21"));
        CompletedWorkout workout3 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout3", user, w, LocalDate.parse("2024-02-20"));
        CompletedWorkout workout4 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout4", user, w, LocalDate.parse("2024-02-21"));

        when(completedWorkoutRepository.findByName("Workout1")).thenReturn(workout1);
        when(completedWorkoutRepository.findByName("Workout2")).thenReturn(workout2);
        when(completedWorkoutRepository.findByName("Workout3")).thenReturn(workout3);
        when(completedWorkoutRepository.findByName("Workout4")).thenReturn(workout4);

        Set set1a = new Set(8, 125);
        Set set1b = new Set(3, 100);
        Set set2 = new Set(7, 130);
        Set set3 = new Set(8, 130);
        Set set4 = new Set(9, 130);
        completedWorkoutService.addSetToLiftSet("Workout1", "Bench Press", set1a, "username");
        completedWorkoutService.addSetToLiftSet("Workout1", "Bench Press", set1b, "username");
        completedWorkoutService.addSetToLiftSet("Workout2", "Bench Press", set2, "username");
        completedWorkoutService.addSetToLiftSet("Workout3", "Bench Press", set3, "username");
        completedWorkoutService.addSetToLiftSet("Workout4", "Bench Press", set4, "username");

        Double result = (Double) userService.compareUserLifts(
                "2024-01-19", "2024-01-22",
                "2024-02-19", "2024-02-22",
                "Bench Press", "username").get("comparison");

        // Validate the result
        assertEquals(.05, result, 0.01);
    }

    @Test
    public void getProgressNoSetsTest() {
        Lift bench = new Lift("Bench Press", "description", "type", "MuscleGroup", "Equipment", "level");
        when(liftRepository.findByTitle("Bench Press")).thenReturn(bench);

        User user = new User("username", "pass", "user@email.com");
        when(userRepository.findByUsername("username")).thenReturn(user);

        Workout w = new Workout("template", "this is used as a template");
        w.addLift(bench);
        when(workoutRepository.save(w)).thenReturn(w);

        CompletedWorkout workout1 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout1", user, w, LocalDate.parse("2024-01-20"));
        CompletedWorkout workout2 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout2", user, w, LocalDate.parse("2024-01-21"));
        CompletedWorkout workout3 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout3", user, w, LocalDate.parse("2024-02-20"));
        CompletedWorkout workout4 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout4", user, w, LocalDate.parse("2024-02-21"));

        when(completedWorkoutRepository.findByName("Workout1")).thenReturn(workout1);
        when(completedWorkoutRepository.findByName("Workout2")).thenReturn(workout2);
        when(completedWorkoutRepository.findByName("Workout3")).thenReturn(workout3);
        when(completedWorkoutRepository.findByName("Workout4")).thenReturn(workout4);

//        Set set1a = new Set(8, 125);
//        Set set1b = new Set(3, 100);
//        Set set2 = new Set(7, 130);
//        Set set3 = new Set(8, 130);
//        Set set4 = new Set(9, 130);
//        completedWorkoutService.addSetToLiftSet("Workout1", "Bench Press", set1a);
//        completedWorkoutService.addSetToLiftSet("Workout1", "Bench Press", set1b);
//        completedWorkoutService.addSetToLiftSet("Workout2", "Bench Press", set2);
//        completedWorkoutService.addSetToLiftSet("Workout3", "Bench Press", set3);
//        completedWorkoutService.addSetToLiftSet("Workout4", "Bench Press", set4);

        Double result = (Double) userService.compareUserLifts(
                "2024-01-19", "2024-01-22",
                "2024-02-19", "2024-02-22",
                "Bench Press", "username").get("comparison");

        // Validate the result
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void getProgressOneSetsTest() {
        Lift bench = new Lift("Bench Press", "description", "type", "MuscleGroup", "Equipment", "level");
        when(liftRepository.findByTitle("Bench Press")).thenReturn(bench);

        User user = new User("username", "pass", "user@email.com");
        when(userRepository.findByUsername("username")).thenReturn(user);

        Workout w = new Workout("template", "this is used as a template");
        w.addLift(bench);
        when(workoutRepository.save(w)).thenReturn(w);

        CompletedWorkout workout1 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout1", user, w, LocalDate.parse("2024-01-20"));
        CompletedWorkout workout2 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout2", user, w, LocalDate.parse("2024-01-21"));
        CompletedWorkout workout3 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout3", user, w, LocalDate.parse("2024-02-20"));
        CompletedWorkout workout4 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout4", user, w, LocalDate.parse("2024-02-21"));

        when(completedWorkoutRepository.findByName("Workout1")).thenReturn(workout1);
        when(completedWorkoutRepository.findByName("Workout2")).thenReturn(workout2);
        when(completedWorkoutRepository.findByName("Workout3")).thenReturn(workout3);
        when(completedWorkoutRepository.findByName("Workout4")).thenReturn(workout4);

        Set set1a = new Set(8, 125);
//        Set set1b = new Set(3, 100);
//        Set set2 = new Set(7, 130);
//        Set set3 = new Set(8, 130);
//        Set set4 = new Set(9, 130);
        completedWorkoutService.addSetToLiftSet("Workout1", "Bench Press", set1a, "username");
//        completedWorkoutService.addSetToLiftSet("Workout1", "Bench Press", set1b);
//        completedWorkoutService.addSetToLiftSet("Workout2", "Bench Press", set2);
//        completedWorkoutService.addSetToLiftSet("Workout3", "Bench Press", set3);
//        completedWorkoutService.addSetToLiftSet("Workout4", "Bench Press", set4);

        Double result = (Double) userService.compareUserLifts(
                "2024-01-19", "2024-01-22",
                "2024-02-19", "2024-02-22",
                "Bench Press", "username").get("comparison");

        // Validate the result
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void getActualMax() {
        Lift bench = new Lift("Bench Press", "description", "type", "MuscleGroup", "Equipment", "level");
        when(liftRepository.findByTitle("Bench Press")).thenReturn(bench);
        ArrayList<Lift> list = new ArrayList<>();
        list.add(bench);

        when(completedWorkoutRepository.getMaxByUserAndContainsKey("username", "Bench Press")).thenReturn(200.0);


        User user = new User("username", "pass", "user@email.com");
        when(userRepository.findByUsername("username")).thenReturn(user);

        Workout w = new Workout("template", "this is used as a template");
        w.addLift(bench);
        when(workoutRepository.save(w)).thenReturn(w);

        CompletedWorkout workout1 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout1", user, w, LocalDate.parse("2024-01-20"));
        CompletedWorkout workout2 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout2", user, w, LocalDate.parse("2024-01-21"));
        CompletedWorkout workout3 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout3", user, w, LocalDate.parse("2024-02-20"));
        CompletedWorkout workout4 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout4", user, w, LocalDate.parse("2024-02-21"));

        when(completedWorkoutRepository.findByName("Workout1")).thenReturn(workout1);
        when(completedWorkoutRepository.findByName("Workout2")).thenReturn(workout2);
        when(completedWorkoutRepository.findByName("Workout3")).thenReturn(workout3);
        when(completedWorkoutRepository.findByName("Workout4")).thenReturn(workout4);

        Set set1a = new Set(8, 125);
        Set set1b = new Set(3, 100);
        Set set2 = new Set(7, 130);
        Set set3 = new Set(8, 130);
        Set set4 = new Set(1, 200);
        completedWorkoutService.addSetToLiftSet("Workout1", "Bench Press", set1a, "username");
        completedWorkoutService.addSetToLiftSet("Workout1", "Bench Press", set1b, "username");
        completedWorkoutService.addSetToLiftSet("Workout2", "Bench Press", set2, "username");
        completedWorkoutService.addSetToLiftSet("Workout3", "Bench Press", set3, "username");
        completedWorkoutService.addSetToLiftSet("Workout4", "Bench Press", set4, "username");

        Double result = (Double) userService.getUserMax("username", "Bench Press").get("maxWeight");

        // Validate the result
        assertEquals(200, result, 0.01);

    }

    @Test
    public void getLastWorkoutTest() {
        // Create a user
        User user = new User("username", "pass", "user@email.com");
        when(userRepository.findByUsername("username")).thenReturn(user);

        // Create a lift
        Lift bench = new Lift("Bench Press", "description", "type", "MuscleGroup", "Equipment", "level");
        when(liftRepository.findByTitle("Bench Press")).thenReturn(bench);

        // Create workouts with different dates
        Workout w = new Workout("template", "this is used as a template");
        w.addLift(bench);
        when(workoutRepository.save(w)).thenReturn(w);

        CompletedWorkout workout1 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout1", user, w, LocalDate.parse("2024-01-20"));
        CompletedWorkout workout2 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout2", user, w, LocalDate.parse("2024-01-21"));
        CompletedWorkout workout3 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout3", user, w, LocalDate.parse("2024-02-20"));

        when(completedWorkoutRepository.findByName("Workout1")).thenReturn(workout1);
        when(completedWorkoutRepository.findByName("Workout2")).thenReturn(workout2);
        when(completedWorkoutRepository.findByName("Workout3")).thenReturn(workout3);

        // Test getting the last workout
        CompletedWorkout lastWorkout = userService.getLastWorkout("username");

        // Validate the result
        assertEquals("Workout3", lastWorkout.getName());
        assertEquals(LocalDate.parse("2024-02-20"), lastWorkout.getDate());
    }

        @Test
        public void compareUserMuscleGroupNoProgressTest() {
        // Create lifts in the same muscle group
        Lift benchPress = new Lift("Bench Press", "description", "type", "Chest", "Equipment", "level");
        Lift inclineBench = new Lift("Incline Bench Press", "description", "type", "Chest", "Equipment", "level");
        when(liftRepository.findByTitle("Bench Press")).thenReturn(benchPress);
        when(liftRepository.findByTitle("Incline Bench Press")).thenReturn(inclineBench);

        // Create user
        User user = new User("username", "pass", "user@email.com");
        when(userRepository.findByUsername("username")).thenReturn(user);

        // Create workout template
        Workout w = new Workout("template", "this is used as a template");
        w.addLift(benchPress);
        w.addLift(inclineBench);
        when(workoutRepository.save(w)).thenReturn(w);

        // Create completed workouts with different dates
        CompletedWorkout workout1 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout1", user, w, LocalDate.parse("2024-01-20"));
        CompletedWorkout workout2 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout2", user, w, LocalDate.parse("2024-01-21"));
        CompletedWorkout workout3 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout3", user, w, LocalDate.parse("2024-02-20"));
        CompletedWorkout workout4 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout4", user, w, LocalDate.parse("2024-02-21"));

        when(completedWorkoutRepository.findByName("Workout1")).thenReturn(workout1);
        when(completedWorkoutRepository.findByName("Workout2")).thenReturn(workout2);
        when(completedWorkoutRepository.findByName("Workout3")).thenReturn(workout3);
        when(completedWorkoutRepository.findByName("Workout4")).thenReturn(workout4);

        // Add sets to workouts
        Set set1a = new Set(8, 125);
        Set set1b = new Set(8, 130);
        Set set2a = new Set(8, 125);
        Set set2b = new Set(8, 130);
        completedWorkoutService.addSetToLiftSet("Workout1", "Bench Press", set1a, "username");
        completedWorkoutService.addSetToLiftSet("Workout1", "Incline Bench Press", set1b, "username");
        completedWorkoutService.addSetToLiftSet("Workout3", "Bench Press", set2a, "username");
        completedWorkoutService.addSetToLiftSet("Workout3", "Incline Bench Press", set2b, "username");

        // Compare muscle group progress
        Double result = (Double) userService.compareUserMuscleGroup(
                "2024-01-19", "2024-01-22",
                "2024-02-19", "2024-02-22",
                "Chest", "username").get("comparison");

        // Validate the result (expect 0% progress)
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void compareUserMuscleGroupNoCommonLiftsTest () {
        // Create lifts in the same muscle group
        Lift benchPress = new Lift("Bench Press", "description", "type", "Chest", "Equipment", "level");
        Lift inclineBench = new Lift("Incline Bench Press", "description", "type", "Chest", "Equipment", "level");
        Lift squats = new Lift("Squats", "description", "type", "Legs", "Equipment", "level");
        when(liftRepository.findByTitle("Bench Press")).thenReturn(benchPress);
        when(liftRepository.findByTitle("Incline Bench Press")).thenReturn(inclineBench);
        when(liftRepository.findByTitle("Squats")).thenReturn(squats);

        // Create user
        User user = new User("username", "pass", "user@email.com");
        when(userRepository.findByUsername("username")).thenReturn(user);

        // Create workout templates
        Workout w1 = new Workout("chest template", "chest workout");
        w1.addLift(benchPress);
        w1.addLift(inclineBench);
        when(workoutRepository.save(w1)).thenReturn(w1);

        Workout w2 = new Workout("leg template", "leg workout");
        w2.addLift(squats);
        when(workoutRepository.save(w2)).thenReturn(w2);

        // Create completed workouts with different dates
        CompletedWorkout workout1 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout1", user, w1, LocalDate.parse("2024-01-20"));
        CompletedWorkout workout2 = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate("Workout3", user, w2, LocalDate.parse("2024-02-20"));

        when(completedWorkoutRepository.findByName("Workout1")).thenReturn(workout1);
        when(completedWorkoutRepository.findByName("Workout3")).thenReturn(workout2);

        // Add sets to workouts
        Set set1a = new Set(8, 125);
        Set set2a = new Set(8, 135);
        completedWorkoutService.addSetToLiftSet("Workout1", "Bench Press", set1a, "username");
        completedWorkoutService.addSetToLiftSet("Workout3", "Squats", set2a, "username");

        // Compare muscle group progress
        Double result = (Double) userService.compareUserMuscleGroup(
                "2024-01-19", "2024-01-22",
                "2024-02-19", "2024-02-22",
                "Chest", "username").get("comparison");

        // Validate the result (expect 0% progress)
        assertEquals(0.0, result, 0.01);
    }

}
