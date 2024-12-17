package com.example.fitnesstracker.models;

public class Lift {
    private String title;
    private String description;
    private String type;
    private String muscleGroup;
    private String equipment;
    private String level;

    // Constructors
    public Lift() {}

    public Lift(String title, String description, String type, String muscleGroup, String equipment, String level) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.level = level;
    }

    // Getters and Setters
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
}
