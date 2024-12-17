package com.example.fitnesstracker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TylerSystemTest {

    @Rule
    public ActivityTestRule<ActivitySignup> activityRule = new ActivityTestRule<>(ActivitySignup.class);

    /**
     * Sign up with valid credentials.
     */
    @Test
    public void testSignUpAndLogin() {
        // Input username
        onView(withId(R.id.etNewUsername)).perform(replaceText("TestUser"));

        // Input password
        onView(withId(R.id.etNewPassword)).perform(replaceText("Password123!"));

        // Confirm password
        onView(withId(R.id.etConfirmPassword)).perform(replaceText("Password123!"));

        // Input email
        onView(withId(R.id.etEmail)).perform(replaceText("testuser@example.com"));

        // Click sign-up button
        onView(withId(R.id.btnSignup)).perform(click());

        // Ensure navigation to login screen
        onView(withId(R.id.login_username_edt)).check(matches(isDisplayed()));

        // Input username
        onView(withId(R.id.login_username_edt)).perform(replaceText("TestUser"));

        // Input password
        onView(withId(R.id.login_password_edt)).perform(replaceText("Password123!"));

        // Click login button
        onView(withId(R.id.login_login_btn)).perform(click());

        // Ensure navigation to home screen
        onView(withId(R.id.btnLiftSearch)).check(matches(isDisplayed()));
    }

    /**
     * Navigate to "My Workouts" and add a new workout.
     */
    @Test
    public void testMyWorkoutsNavigationAndAddWorkout() {
        testSignUpAndLogin(); // Ensure user is signed up and logged in.

        // Navigate to My Workouts
        onView(withId(R.id.btnLiftSearch)).perform(click());

        // Click Add Workout
        onView(withId(R.id.btnAddWorkout)).perform(click());
    }

    /**
     * Add a lift to a new workout.
     */
    @Test
    public void testAddLiftToNewWorkout() {
        testSignUpAndLogin(); // Ensure user is signed up and logged in.

        // Navigate to Add Lift
        onView(withId(R.id.btnAddLiftBelow)).perform(click());

        // Select the first lift in the list
        onView(withText("Bench Press")).perform(click());
    }

    /**
     * Edit a workout and add a new lift.
     */
    @Test
    public void testEditWorkoutAndAddLift() {
        testSignUpAndLogin(); // Ensure user is signed up and logged in.

        // Select a workout
        onView(withText("Leg Day")).perform(click());

        // Click Add Lift
        onView(withId(R.id.btnAddLift)).perform(click());

        // Select a lift
        onView(withText("Deadlift")).perform(click());
    }

    /**
     * Start a workout and complete it.
     */
    @Test
    public void testStartWorkoutAndComplete() {
        testSignUpAndLogin(); // Ensure user is signed up and logged in.

        // Start a workout
        onView(withText("Leg Day")).perform(click());

        // Add sets and reps
        onView(withId(R.id.etWeight)).perform(replaceText("3"));
        onView(withId(R.id.etReps)).perform(replaceText("10"));

        // Complete the workout
        onView(withId(R.id.btnCompleteWorkout)).perform(click());
    }

    /**
     * View a completed workout in the calendar.
     */
    @Test
    public void testViewRecentCompletedWorkoutInCalendar() {
        testSignUpAndLogin(); // Ensure user is signed up and logged in.

        // Navigate to Calendar
        onView(withId(R.id.btnCalendar)).perform(click());

        // Select a day
        onView(withText("2")).perform(click());

        // Verify workout is displayed
        onView(withText("Leg Day")).check(matches(isDisplayed()));
    }
}
