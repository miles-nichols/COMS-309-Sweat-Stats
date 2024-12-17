package coms309.workout.CompletedWorkout;

import coms309.workout.Users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompletedWorkoutControllerTests
{
    @Mock
    private CompletedWorkoutService completedWorkoutService;

    @InjectMocks
    private CompletedWorkoutController completedWorkoutController;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void testGetAllCompletedWorkout()
    {
        User u = new User("u","u","u");
        User u2 = new User("u2","u2","u2");
        List<CompletedWorkout> completedWorkouts = Arrays.asList(
                new CompletedWorkout("test0", u),
                new CompletedWorkout("test1", u2)
        );
        when(completedWorkoutService.getAllCompletedWorkouts()).thenReturn(completedWorkouts);

        List<CompletedWorkout> result = completedWorkoutController.getAllCompletedWorkout();

        assertEquals(2, result.size());
        verify(completedWorkoutService, times(1)).getAllCompletedWorkouts();
    }

    @Test
    void testGetAllCompletedWorkoutsForUser()
    {
        User u = new User("u","u","u");
        List<CompletedWorkout> completedWorkouts = Arrays.asList(
                new CompletedWorkout("test0", u),
                new CompletedWorkout("test1", u)
        );
        when(completedWorkoutService.getCompletedWorkoutsForUser("u")).thenReturn(completedWorkouts);

        List<CompletedWorkout> result = completedWorkoutController.getCompletedWorkoutsForUser("u");

        assertEquals(2, result.size());
        verify(completedWorkoutService, times(1)).getCompletedWorkoutsForUser("u");
    }
}
