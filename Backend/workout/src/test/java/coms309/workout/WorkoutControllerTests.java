package coms309.workout.Workout;

import com.fasterxml.jackson.databind.ObjectMapper;
import coms309.workout.Lifts.Lift;
import coms309.workout.Lifts.LiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkoutControllerTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private LiftRepository liftRepository;

    @InjectMocks
    private WorkoutController workoutController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllWorkouts_shouldReturnListOfWorkouts() {
        // Arrange
        Workout workout1 = new Workout();
        workout1.setName("Workout 1");
        Workout workout2 = new Workout();
        workout2.setName("Workout 2");

        when(workoutRepository.findAll()).thenReturn(Arrays.asList(workout1, workout2));

        // Act
        List<Workout> result = workoutController.getAllWorkouts();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Workout 1", result.get(0).getName());
        assertEquals("Workout 2", result.get(1).getName());
        verify(workoutRepository, times(1)).findAll();
    }

    @Test
    void getWorkoutByName_shouldReturnWorkout_whenFound() {
        // Arrange
        Workout workout = new Workout();
        workout.setName("Cardio");

        when(workoutRepository.findByName("Cardio")).thenReturn(workout);

        // Act
        Workout result = (Workout) workoutController.getWorkoutByName("Cardio").get("workout");

        // Assert
        assertNotNull(result);
        assertEquals("Cardio", result.getName());
        verify(workoutRepository, times(1)).findByName("Cardio");
    }

    @Test
    void getWorkoutByName_shouldReturnNull_whenNotFound() {
        // Arrange
        when(workoutRepository.findByName("Unknown")).thenReturn(null);

        // Act
        Workout result =(Workout) workoutController.getWorkoutByName("Unknown").get("workout");

        // Assert
        assertNull(result);
        verify(workoutRepository, times(1)).findByName("Unknown");
    }

    @Test
    void addLiftToWorkout_shouldAddLift_whenValidWorkoutAndLift() {
        // Arrange
        Workout workout = new Workout();
        workout.setName("Workout 1");
        Lift lift = new Lift();
        lift.setTitle("Bench Press");

        when(workoutRepository.findByName("Workout 1")).thenReturn(workout);
        when(liftRepository.findByTitle("Bench Press")).thenReturn(lift);

        // Act
        String result = workoutController.addLiftToWorkout("Workout 1", "Bench Press").get("status");

        // Assert
        assertNotNull(result);
        assertTrue(result.equals("200"));
        verify(workoutRepository, times(1)).save(workout);
    }
    

    @Test
    void deleteWorkoutByName_shouldDeleteWorkout_whenFound() {
        // Arrange
        Workout workout = new Workout();
        workout.setName("Workout 1");

        when(workoutRepository.findByName("Workout 1")).thenReturn(workout);

        // Act
        workoutController.deleteWorkoutByName("Workout 1");

        // Assert
        verify(workoutRepository, times(1)).delete(workout);
    }


}
