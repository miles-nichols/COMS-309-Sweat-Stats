package com.example.fitnesstracker;

/**
 * Represents a user in the fitness tracker app, including their username and details of their last workout.
 *
 * @author Miles Nichols
 */
public class ObjectUser {
    private String username;
    private String lastWorkout;

    /**
     * Gets the username of the user.
     *
     * @return The user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The username to set for the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the last workout completed by the user.
     *
     * @return A description or name of the last workout.
     */
    public String getLastWorkout() {
        return lastWorkout;
    }

    /**
     * Sets the last workout completed by the user.
     *
     * @param lastWorkout A description or name of the last workout to set.
     */
    public void setLastWorkout(String lastWorkout) {
        this.lastWorkout = lastWorkout;
    }
}
