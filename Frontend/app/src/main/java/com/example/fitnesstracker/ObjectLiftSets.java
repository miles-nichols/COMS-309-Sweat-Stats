// ObjectLiftSets.java

package com.example.fitnesstracker;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Represents a collection of sets for a specific lift in the fitness tracker.
 * Each set contains reps and weight information, associated with a particular lift.
 * Includes a unique liftSetId.
 */
public class ObjectLiftSets implements Serializable {
    private String liftSetId; // Unique identifier for the lift set
    private String name;       // Name of the lift
    private List<ObjectSet> sets;

    /**
     * Constructor to create an ObjectLiftSets instance with a unique ID.
     *
     * @param name The name of the lift.
     * @param sets A list of sets associated with this lift.
     */
    public ObjectLiftSets(String name, List<ObjectSet> sets) {
        this.liftSetId = UUID.randomUUID().toString(); // Generate unique ID
        this.name = name;
        this.sets = sets;
    }

    /**
     * Constructor to create an ObjectLiftSets instance with an existing liftSetId.
     *
     * @param liftSetId The existing liftSetId.
     * @param name      The name of the lift.
     * @param sets      A list of sets associated with this lift.
     */
    public ObjectLiftSets(String liftSetId, String name, List<ObjectSet> sets) {
        this.liftSetId = liftSetId; // Use existing liftSetId
        this.name = name;
        this.sets = sets;
    }

    // Getter and Setter methods

    public String getLiftSetId() {
        return liftSetId;
    }

    public void setLiftSetId(String liftSetId) {
        this.liftSetId = liftSetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ObjectSet> getSets() {
        return sets;
    }

    public void setSets(List<ObjectSet> sets) {
        this.sets = sets;
    }

    // Override toString() for better readability
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lift: ").append(name).append("\n");
        for (int i = 0; i < sets.size(); i++) {
            ObjectSet set = sets.get(i);
            sb.append("  Set ").append(i + 1).append(": ")
                    .append(set.getReps()).append(" reps @ ")
                    .append(set.getWeight()).append(" kg\n");
        }
        return sb.toString().trim();
    }
}
