package coms309.workout.Workout;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import coms309.workout.Lifts.Lift;

@Entity
@Table(name = "workout")
public class Workout
{
    //Stores id of the workout
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //Stores the name of the workout (example: Arms)
    @Lob
    @Column(columnDefinition="Text")
    private String name;

    //Description of the workout being done
    @Lob
    @Column(columnDefinition="Text")
    private String description;

    //Stores lifts included in the workout
//    @OneToMany(cascade = CascadeType.ALL)
//    @JoinColumn(name = "workout_id")
//    private List<Lift> lifts;

    @ManyToMany
    @JoinTable(name = "workout_lifts", joinColumns = { @JoinColumn(name = "workout_id") }, inverseJoinColumns = {@JoinColumn(name = "lift_id") } )
    private Set<Lift> lifts = new HashSet<Lift>();

    //Generic Constructor to not be used
    public Workout() {}

    //Constructor to be used
    public Workout(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public void setDescription(String description) {this.description = description;}

    public void addLift(Lift lift) {lifts.add(lift);}

    //    public Lift getLiftByIndex(int index) {return lifts.get(index);}

    public Set<Lift> getLifts() {return lifts;}

}