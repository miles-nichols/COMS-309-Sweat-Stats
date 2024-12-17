// ObjectActiveWorkout.java

package com.example.fitnesstracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an active workout session, including its name, date, and associated lift sets.
 */
public class ObjectActiveWorkout implements Serializable {
    private String name;
    private Date date;
    private List<ObjectLiftSets> liftSets;

    public ObjectActiveWorkout(String name) {
        this.name = name;
        this.liftSets = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<ObjectLiftSets> getLiftSets() {
        return liftSets;
    }

    public void setLiftSets(List<ObjectLiftSets> liftSets) {
        this.liftSets = liftSets;
    }

    public void addLiftSet(ObjectLiftSets liftSet) {
        this.liftSets.add(liftSet);
    }
}
