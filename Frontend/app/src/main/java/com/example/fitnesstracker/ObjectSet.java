package com.example.fitnesstracker;

import java.io.Serializable;

/**
 * Represents a set in a workout, consisting of the number of repetitions (reps) and the weight used.
 *
 * @author
 */
public class ObjectSet implements Serializable {
    private int reps;
    private double weight;

    /**
     * Constructor to initialize the set with reps and weight.
     *
     * @param reps   The number of repetitions for the set.
     * @param weight The weight used for the set in kilograms.
     */
    public ObjectSet(int reps, double weight) {
        this.reps = reps;
        this.weight = weight;
    }

    /**
     * Gets the number of repetitions in the set.
     *
     * @return The number of reps.
     */
    public int getReps() {
        return reps;
    }

    /**
     * Sets the number of repetitions for the set.
     *
     * @param reps The number of reps to set.
     */
    public void setReps(int reps) {
        this.reps = reps;
    }

    /**
     * Gets the weight used in the set.
     *
     * @return The weight in kilograms.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight used for the set.
     *
     * @param weight The weight in kilograms to set.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    // Override toString() for better readability
    @Override
    public String toString() {
        return reps + " reps @ " + weight + " kg";
    }
}
