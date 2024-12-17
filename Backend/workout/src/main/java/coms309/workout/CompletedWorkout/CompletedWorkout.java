package coms309.workout.CompletedWorkout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import coms309.workout.ActivityFeed.CompletedWorkoutListener;
import coms309.workout.LiftSet.LiftSet;
import coms309.workout.Lifts.Lift;
import coms309.workout.Users.User;
import jakarta.persistence.*;
import java.util.*;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@EntityListeners(CompletedWorkoutListener.class)
@Table(name = "completed_workout")
public class CompletedWorkout
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(columnDefinition = "Text")
    private String name;

    @Lob
    @Column(name = "Date")
    private LocalDate date;

    @OneToMany(mappedBy = "completedWorkout", cascade = CascadeType.ALL)
    private List<LiftSet> completedSets = new ArrayList<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    //Generic Constructor not to be used
    public CompletedWorkout() {}

    //Contructor to be used
    public CompletedWorkout(String name, User user)
    {
        this.name = name;
        this.date = LocalDate.now();
        this.user = user;
        this.date = LocalDate.now();
    }

    public CompletedWorkout(String name, User user, LocalDate d)
    {
        this.name = name;
        this.date = d;
        this.user = user;
    }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public LocalDate getDate() {return date;}

    public void setDate(LocalDate date) {this.date = date;}

    public List<LiftSet> getCompletedSets() {return completedSets;}

    public void setCompletedSets(List<LiftSet> completedSets) { this.completedSets = completedSets; }

    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}

//    public void addSetToLift(Set set, Lift lift) {completedSets.get(lift).addSet(set);}

//    public void addSetToLiftByData(int reps, double weight, Lift lift)
//    {
//        completedSets.get(lift).addSetByData(reps, weight);
//    }
    public List<LiftSet> getLifts(Lift l){
        List<LiftSet> liftSets = new ArrayList<>();
        for(LiftSet ls : completedSets){
            if(ls.getLift().equals(l)){
                liftSets.add(ls);
            }
        }
        return liftSets;

    }
}