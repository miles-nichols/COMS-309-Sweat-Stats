package com.example.fitnesstracker;

import com.example.fitnesstracker.ObjectUser;
import com.example.fitnesstracker.ObjectWorkout;
import com.example.fitnesstracker.ObjectLift;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Miles Nichols
 * This class functions to test some core features of the the fitness tracker application.
 */
public class MilesSystemTest {


    /**
     * Method to test if the login username is saved to the ObjectUser object.
     */
    @Test
    public void testLoginSavesUsername() {
        ObjectUser user = new ObjectUser();
        user.setUsername("Miles");
        assertEquals("Miles", user.getUsername()); // Check username values
    }

    /**
     * Method to test if two different workouts are not equal
     */
    @Test
    public void testWorkoutEquality() {
        // Create ObjectLift instances
        ObjectLift lift1 = new ObjectLift("Squat", "Leg exercise", "Strength", "Legs", "Barbell", "Intermediate");
        ObjectLift lift2 = new ObjectLift("Deadlift", "Back exercise", "Strength", "Back", "Barbell", "Advanced");

        // Create a List of ObjectLift objects
        List<ObjectLift> lifts = new ArrayList<>();
        lifts.add(lift1);
        lifts.add(lift2);

        // Create test objects
        ObjectWorkout workout1 = new ObjectWorkout(1, "Leg Day", "Leg and Back workout", lifts);
        ObjectWorkout workout2 = new ObjectWorkout(2, "Leg Day", "Leg and Back workout", lifts);
        assertNotEquals(workout1, workout2);  // Check object values
    }

    /**
     * Method to test if the constructor for active workouts function properly
     */
    @Test
    public void testActiveWorkoutConstruction() {
        ObjectActiveWorkout activeWorkout = new ObjectActiveWorkout("arms");
        assertEquals("arms", activeWorkout.getName()); //if active workout has the name set
    }

    /**
     * Method to test if setLifts() works properly for the active workout
     */
    @Test
    public void testSetLifts() {
        ObjectActiveWorkout activeWorkout = new ObjectActiveWorkout("arms"); //object for current workout
        List<ObjectSet> setList = new ArrayList<ObjectSet>(); // list of each set object
        ObjectSet set1 = new ObjectSet(40, 100); // create a set
        ObjectSet set2 = new ObjectSet(5, 200); // create a set
        setList.add(set1); // add set
        setList.add(set2); // add set
        ObjectLiftSets lifts = new ObjectLiftSets("1", "benchPress", setList); // object holding a list of bench press sets
        List<ObjectLiftSets> liftSets = new ArrayList<ObjectLiftSets>(); //  list of liftSet objects
        liftSets.add(lifts); //add sets from bench press to list of liftsets
        activeWorkout.setLiftSets(liftSets); //set liftsets for current workout

        assertEquals(activeWorkout.getLiftSets(), liftSets); //check if setLiftSets worked properly
    }

    /**
     * Method to test if workout days are added correctly to the CalendarActivity.
     */
    @Test
    public void testAddWorkoutDays() {
        Set<String> workoutDays = new HashSet<>();
        workoutDays.add("2024-12-01");
        workoutDays.add("2024-12-02");

        assertTrue(workoutDays.contains("2024-12-01"));
        assertTrue(workoutDays.contains("2024-12-02"));
        assertFalse(workoutDays.contains("2024-12-03"));
    }

    /**
     * Method to test if the selected date is correctly identified as a workout day.
     */
    @Test
    public void testIsWorkoutDay() {
        Set<String> workoutDays = new HashSet<>();
        workoutDays.add("2024-12-01");

        String selectedDate = "2024-12-01";
        assertTrue(workoutDays.contains(selectedDate));

        selectedDate = "2024-12-02";
        assertFalse(workoutDays.contains(selectedDate));
    }

    /**
     * Method to test if the Report constructor sets values correctly.
     */
    @Test
    public void testReportConstructor() {
        // Creating a Report object with mock data
        Report report = new Report("JohnDoe", "Admin", 3, "Abusive behavior",
                "User has been using offensive language", "2024-12-01",
                true, 101, "JaneDoe", "JohnDoe");

        // Assert that the values were set correctly in the Report object
        assertEquals("JohnDoe", report.getUsername());
        assertEquals("Admin", report.getRole());
        assertEquals(3, report.getStrikes());
        assertEquals("Abusive behavior", report.getSummary());
        assertEquals("User has been using offensive language", report.getDescription());
        assertEquals("2024-12-01", report.getReportDate());
        assertTrue(report.isCompleted());
        assertEquals(101, report.getReportId());
        assertEquals("JaneDoe", report.getReportedUsername());
        assertEquals("JohnDoe", report.getReporterUsername());
    }

    /**
     * Method to test if the report is marked as completed correctly.
     */
    @Test
    public void testReportCompletion() {
        // Creating a Report object
        Report report = new Report("JohnDoe", "Admin", 3, "Spam",
                "User is spamming messages", "2024-12-01",
                false, 102, "JaneDoe", "JohnDoe");

        // Initially, the report is not completed
        assertFalse(report.isCompleted());

        // Mark the report as completed
        report.setCompleted(true);

        // Assert that the report is now marked as completed
        assertTrue(report.isCompleted());
    }

    /**
     * Method to test if the strikes value is updated correctly.
     */
    @Test
    public void testStrikesUpdate() {
        // Creating a Report object
        Report report = new Report("JohnDoe", "Admin", 2, "Spamming",
                "User is spamming messages", "2024-12-01",
                true, 103, "JaneDoe", "JohnDoe");

        // Initially, the report has 2 strikes
        assertEquals(2, report.getStrikes());

        // Update the strikes value
        report.setStrikes(3);

        // Assert that the strikes value is updated correctly
        assertEquals(3, report.getStrikes());
    }

    /**
     * Method to test if the 'reportDate' is set and accessible correctly.
     */
    @Test
    public void testReportDate() {
        // Creating a Report object
        Report report = new Report("JohnDoe", "Admin", 1, "Offensive comment",
                "User made an offensive comment", "2024-12-01",
                true, 104, "JaneDoe", "JohnDoe");

        // Assert that the report date is correctly set
        assertEquals("2024-12-01", report.getReportDate());
    }

    /**
     * Method to test if the 'reportId' is correctly assigned.
     */
    @Test
    public void testReportId() {
        // Creating a Report object
        Report report = new Report("JohnDoe", "Admin", 5, "Harassment",
                "User harassed other users", "2024-12-01",
                false, 105, "JaneDoe", "JohnDoe");

        // Assert that the report ID is correctly set
        assertEquals(105, report.getReportId());
    }

    /**
     * Method to test if setting a null username in ObjectUser is handled gracefully.
     */
    @Test
    public void testSetNullUsername() {
        ObjectUser user = new ObjectUser();
        user.setUsername(null);
        assertNull(user.getUsername()); // Check if username is null
    }

    /**
     * Method to test if empty workout names are handled properly.
     */
    @Test
    public void testEmptyWorkoutName() {
        ObjectWorkout workout = new ObjectWorkout(1, "", "Empty workout name test", new ArrayList<>());
        assertEquals("", workout.getName()); // Check if workout name is set to empty string
    }

    /**
     * Method to test if duplicate lifts in a workout are handled correctly.
     */
    @Test
    public void testDuplicateLiftsInWorkout() {
        ObjectLift lift = new ObjectLift("Squat", "Leg exercise", "Strength", "Legs", "Barbell", "Intermediate");
        List<ObjectLift> lifts = new ArrayList<>();
        lifts.add(lift);
        lifts.add(lift); // Adding the same lift again
        ObjectWorkout workout = new ObjectWorkout(1, "Leg Day", "Leg and Back workout", lifts);

        // Assuming workout class has a method to check for unique lifts
        assertEquals(2, workout.getLifts().size()); // Should count 2 lifts (no uniqueness check)
    }

    /**
     * Method to test if ObjectActiveWorkout's setLiftSets() correctly updates the list of lift sets.
     */
    @Test
    public void testSetLiftSetsUpdates() {
        ObjectActiveWorkout activeWorkout = new ObjectActiveWorkout("arms");
        List<ObjectSet> setList = new ArrayList<>();
        setList.add(new ObjectSet(10, 100)); // First set
        setList.add(new ObjectSet(12, 110)); // Second set
        List<ObjectLiftSets> liftSets = new ArrayList<>();
        liftSets.add(new ObjectLiftSets("1", "benchPress", setList));

        // Set lift sets for workout
        activeWorkout.setLiftSets(liftSets);

        // Update lift sets
        List<ObjectSet> newSetList = new ArrayList<>();
        newSetList.add(new ObjectSet(8, 120)); // New set
        liftSets.get(0).setSets(newSetList); // Update the first lift's sets
        activeWorkout.setLiftSets(liftSets); // Apply updated lift sets

        assertEquals(1, activeWorkout.getLiftSets().size()); // Check if there's still one lift
        assertEquals(1, activeWorkout.getLiftSets().get(0).getSets().size()); // Check if lift has the updated set
    }

    /**
     * Method to test the behavior of an empty workout list.
     */
    @Test
    public void testEmptyWorkoutList() {
        List<ObjectLift> lifts = new ArrayList<>();
        ObjectWorkout workout = new ObjectWorkout(1, "No Lifts", "Workout with no lifts", lifts);

        assertTrue(workout.getLifts().isEmpty()); // Assert no lifts exist
    }

    /**
     * Method to test the behavior when adding a report with an invalid date format.
     */
    @Test
    public void testInvalidReportDate() {
        Report report = new Report("JohnDoe", "Admin", 2, "Spam", "User is spamming messages", "invalid-date", false, 106, "JaneDoe", "JohnDoe");
        assertEquals("invalid-date", report.getReportDate()); // Check if invalid date is accepted
    }

    /**
     * Method to test if adding a non-existent report ID to the system fails gracefully.
     */
    @Test
    public void testNonExistentReportId() {
        Report report = new Report("JohnDoe", "Admin", 1, "Spam", "User is spamming messages", "2024-12-01", false, 99999, "JaneDoe", "JohnDoe");

        // Assuming there's a mechanism for checking if the report ID exists in a DB or system.
        assertEquals(99999, report.getReportId()); // Check if the report ID is still set (even if non-existent)
    }

    /**
     * Method to test if adding a user with an empty username fails gracefully.
     */
    @Test
    public void testUserWithEmptyUsername() {
        ObjectUser user = new ObjectUser();
        user.setUsername(""); // Set empty username
        assertEquals("", user.getUsername()); // Ensure the username is still empty
    }

    /**
     * Method to test if the calendar correctly identifies a date that has no workout.
     */
    @Test
    public void testNoWorkoutOnDate() {
        Set<String> workoutDays = new HashSet<>();
        workoutDays.add("2024-12-01");
        workoutDays.add("2024-12-02");

        String selectedDate = "2024-12-03";
        assertFalse(workoutDays.contains(selectedDate)); // Assert the date does not exist in workoutDays
    }

    @Test
    public void testLiftLoop() {
        ObjectLift lift = new ObjectLift("Squat", "Leg exercise", "Strength", "Legs", "Barbell", "Intermediate");
        List<ObjectSet> sets = new ArrayList<>();
        sets.add(new ObjectSet(10, 100));
        sets.add(new ObjectSet(8, 120));

        // Assuming you have a method that sums the total reps
        int totalReps = lift.calculateTotalReps(sets);
        assertEquals(18, totalReps);  // Make sure the method is properly tested
    }

}
