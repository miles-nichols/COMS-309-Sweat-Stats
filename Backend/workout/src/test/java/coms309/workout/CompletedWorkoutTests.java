package coms309.workout.CompletedWorkout;

import coms309.workout.LiftSet.LiftSet;
import coms309.workout.Lifts.Lift;
import coms309.workout.Users.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompletedWorkoutTests {
    @Test
    void testGetAndSetName() {
        CompletedWorkout completedWorkout = new CompletedWorkout();
        completedWorkout.setName("Test");
        assertEquals("Test", completedWorkout.getName());
    }

    @Test
    void testGetAndSetDate() {
        CompletedWorkout completedWorkout = new CompletedWorkout();
        completedWorkout.setDate(LocalDate.of(2024, 12, 25));
        assertEquals(LocalDate.of(2024, 12, 25), completedWorkout.getDate());
    }

    @Test
    void testGetAndSetUser() {
        CompletedWorkout completedWorkout = new CompletedWorkout();
        User u = new User("u", "u", "u");
        completedWorkout.setUser(u);
        assertEquals(u, completedWorkout.getUser());
    }

    @Test
    void testConstructor()
    {
        User u = new User("u", "u", "u");
        CompletedWorkout completedWorkout = new CompletedWorkout("test", u);
        assertEquals(u, completedWorkout.getUser());
        assertEquals("test", completedWorkout.getName());
    }

    @Test
    void testConstructorWithDate()
    {
        User u = new User("u", "u", "u");
        LocalDate d = LocalDate.of(2024, 12, 25);
        CompletedWorkout completedWorkout = new CompletedWorkout("test", u, d);
        assertEquals(u, completedWorkout.getUser());
        assertEquals("test", completedWorkout.getName());
        assertEquals(d, completedWorkout.getDate());
    }
}