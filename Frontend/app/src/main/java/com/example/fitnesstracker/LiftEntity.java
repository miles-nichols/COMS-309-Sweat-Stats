package com.example.fitnesstracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a lift exercise with its associated details and sets.
 * Each lift includes information such as title, description, type, and sets of reps/weights.
 * This class can be used to manage data for various workouts in a fitness app.
 */
public class LiftEntity implements Serializable {

    private String title;
    private String description;
    private String type;
    private String muscleGroup;
    private String equipment;
    private String level;
    private List<SetEntity> sets;

    /**
     * Constructor to create a new LiftEntity instance with all details and sets.
     *
     * @param title        The title of the lift.
     * @param description  The description of the lift.
     * @param type         The type of the lift (e.g., Strength, Cardio).
     * @param muscleGroup  The muscle group targeted by the lift.
     * @param equipment    The equipment needed for the lift.
     * @param level        The difficulty level of the lift.
     * @param sets         The list of sets associated with the lift.
     */
    public LiftEntity(String title, String description, String type, String muscleGroup,
                      String equipment, String level, List<SetEntity> sets) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.level = level;
        this.sets = sets != null ? sets : new ArrayList<>();
    }

    /**
     * Constructor to create a LiftEntity with no initial sets.
     *
     * @param title        The title of the lift.
     * @param description  The description of the lift.
     * @param type         The type of the lift.
     * @param muscleGroup  The muscle group targeted by the lift.
     * @param equipment    The equipment needed for the lift.
     * @param level        The difficulty level of the lift.
     */
    public LiftEntity(String title, String description, String type, String muscleGroup, String equipment, String level) {
        this(title, description, type, muscleGroup, equipment, level, new ArrayList<>());
    }

    // Getters and setters for the fields
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<SetEntity> getSets() {
        return sets;
    }

    public void setSets(List<SetEntity> sets) {
        this.sets = sets;
    }

    /**
     * Adds a new set to the list of sets for this lift.
     *
     * @param set The new SetEntity to add.
     */
    public void addSet(SetEntity set) {
        sets.add(set);
    }

    /**
     * Provides a string representation of the LiftEntity for debugging purposes.
     *
     * @return A string containing the details of the lift and its sets.
     */
    @Override
    public String toString() {
        return "LiftEntity{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", muscleGroup='" + muscleGroup + '\'' +
                ", equipment='" + equipment + '\'' +
                ", level='" + level + '\'' +
                ", sets=" + sets +
                '}';
    }

    /**
     * Represents a set in a workout, consisting of repetitions (reps) and weight used.
     */
    public static class SetEntity implements Serializable {
        private int reps;
        private double weight;

        /**
         * Constructor to create a new SetEntity instance.
         *
         * @param reps   The number of repetitions for the set.
         * @param weight The weight used for the set.
         */
        public SetEntity(int reps, double weight) {
            this.reps = reps;
            this.weight = weight;
        }

        // Getters and setters for the fields
        public int getReps() {
            return reps;
        }

        public void setReps(int reps) {
            this.reps = reps;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        /**
         * Provides a string representation of the SetEntity for debugging purposes.
         *
         * @return A string containing the details of the set.
         */
        @Override
        public String toString() {
            return "SetEntity{" +
                    "reps=" + reps +
                    ", weight=" + weight +
                    '}';
        }
    }
}
