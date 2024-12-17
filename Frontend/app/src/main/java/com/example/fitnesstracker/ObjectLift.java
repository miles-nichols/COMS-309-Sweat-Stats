// ObjectLift.java

package com.example.fitnesstracker;

import java.io.Serializable;
import java.util.List;

/**
 * Class representing a lift exercise in the fitness tracker.
 * It contains details such as title, description, type, muscle group, equipment, and level.
 */
public class ObjectLift implements Serializable {

    private String title;
    private String description;
    private String type;
    private String muscleGroup;
    private String equipment;
    private String level;

    /**
     * Constructor to create an ObjectLift instance.
     *
     * @param title        The title of the lift.
     * @param description  The description of the lift.
     * @param type         The type of lift (e.g., Strength, Cardio).
     * @param muscleGroup  The muscle group targeted by the lift.
     * @param equipment    The equipment needed for the lift.
     * @param level        The difficulty level of the lift.
     */
    public ObjectLift(String title, String description, String type, String muscleGroup, String equipment, String level) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.level = level;
    }

    /**
     * Gets the title of the lift.
     *
     * @return The title of the lift.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the lift.
     *
     * @param title The title to set for the lift.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of the lift.
     *
     * @return The description of the lift.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the lift.
     *
     * @param description The description to set for the lift.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the type of lift (e.g., Strength, Cardio).
     *
     * @return The type of lift.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of lift.
     *
     * @param type The type to set for the lift.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the muscle group targeted by the lift.
     *
     * @return The muscle group targeted.
     */
    public String getMuscleGroup() {
        return muscleGroup;
    }

    /**
     * Sets the muscle group targeted by the lift.
     *
     * @param muscleGroup The muscle group to set.
     */
    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    /**
     * Gets the equipment required for the lift.
     *
     * @return The equipment required for the lift.
     */
    public String getEquipment() {
        return equipment;
    }

    /**
     * Sets the equipment required for the lift.
     *
     * @param equipment The equipment to set for the lift.
     */
    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    /**
     * Gets the difficulty level of the lift.
     *
     * @return The difficulty level of the lift.
     */
    public String getLevel() {
        return level;
    }

    /**
     * Sets the difficulty level of the lift.
     *
     * @param level The level to set for the lift.
     */
    public void setLevel(String level) {
        this.level = level;
    }

    public int calculateTotalReps(List<ObjectSet> sets) {
        int totalReps = 0;
        for (ObjectSet set : sets) {
            totalReps += set.getReps();  // Assuming ObjectSet has a getReps() method
        }
        return totalReps;
    }
}
