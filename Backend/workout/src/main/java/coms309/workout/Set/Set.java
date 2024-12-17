package coms309.workout.Set;

import coms309.workout.CompletedWorkout.CompletedWorkout;
import coms309.workout.Lifts.Lift;
import jakarta.persistence.*;
import java.util.*;

@Embeddable
public class Set
{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;

    private int reps;

    private double weight;

//    @ManyToOne
//    @JoinColumn(name = "lift_id")
//    private Lift lift;
//
//    @ManyToOne
//    @JoinColumn(name = "completed_workout_id")
//    private CompletedWorkout completedWorkout;

    public Set() {}

    public Set(int reps, double weight)
    {
        this.reps = reps;
        this.weight = weight;
    }

    public int getReps() { return reps; }

    public void setReps(int reps) { this.reps = reps; }

    public double getWeight() { return weight; }

    public void setWeight(double weight) { this.weight = weight; }

    public double calculateOneRepMax(){
        return weight*(1 + reps/(double)(30));
    }
}