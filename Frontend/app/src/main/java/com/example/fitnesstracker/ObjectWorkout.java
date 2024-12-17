// ObjectWorkout.java

package com.example.fitnesstracker;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a workout, including its ID, name, description, and associated lifts.
 */
public class ObjectWorkout implements Serializable {
    private int id;
    private String name;
    private String description;
    private List<ObjectLift> lifts;

    public ObjectWorkout(int id, String name, String description, List<ObjectLift> lifts) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lifts = lifts;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ObjectLift> getLifts() {
        return lifts;
    }

    public void setLifts(List<ObjectLift> lifts) {
        this.lifts = lifts;
    }
}
