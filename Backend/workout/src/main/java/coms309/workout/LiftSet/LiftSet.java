package coms309.workout.LiftSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import coms309.workout.CompletedWorkout.CompletedWorkout;
import coms309.workout.Lifts.Lift;
import coms309.workout.Set.Set;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "lift_sets")
public class LiftSet
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn
    private Lift lift;

    @ElementCollection
    private List<Set> sets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "completed_workout_id")
    @JsonIgnore
    private CompletedWorkout completedWorkout;

    public LiftSet() {}

    public LiftSet(Lift lift)
    {
        this.lift = lift;
    }

    public List<Set> getSets() { return sets; }

    public void setSets(List<Set> sets) { this.sets = sets; }

    public Lift getLift() { return lift; }

    public void setLift(Lift lift) { this.lift = lift; }

    public CompletedWorkout getCompletedWorkout() { return completedWorkout; }

    public void setCompletedWorkout(CompletedWorkout completedWorkout) { this.completedWorkout = completedWorkout; }

    public void addSet(Set set) { this.sets.add(set); }

    public void addSetByData(int reps, double weight)
    {
        this.sets.add(new Set(reps, weight));
    }

    public Set findBestSet(){
        if(sets.isEmpty()) return null;
        Set bestSet = sets.get(0);
        for(int i = 1; i < sets.size(); i++){
            if(bestSet.calculateOneRepMax()<sets.get(i).calculateOneRepMax()){
                bestSet = sets.get(i);
            }
        }
        return bestSet;
    }
}
