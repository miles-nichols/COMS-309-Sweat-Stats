package coms309.workout.Lifts;

import jakarta.persistence.*;

@Entity
@Table(name = "lifts")
public class Lift
{
    //Title variable is used to store the title of the lift
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String title;

    //Description variable is used to describe the lift
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    //Used to declare the type of lift (strength, stretching, etc.)
    @Lob
    @Column(columnDefinition = "TEXT")
    private String type;

    //Holds the muscle group that the lift is meant to work
    @Lob
    @Column(columnDefinition = "TEXT", name = "muscleGroup")
    private String muscleGroup;

    //Used to describe the equipment needed for the lift
    @Lob
    @Column(columnDefinition = "TEXT")
    private String equipment;

    //Level of difficulty to perform the lift
    @Lob
    @Column(columnDefinition = "TEXT")
    private String level;

    //General Constructor not to be used
    public Lift() {}

    //Actual Constructor
    public Lift(String title, String description, String type, String muscleGroup, String equipment, String level)
    {
        this.title = title;
        this.description = description;
        this.type = type;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.level = level;
    }

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}

    public String getMuscleGroup() {return muscleGroup;}

    public void setMuscleGroup(String muscleGroup) {this.muscleGroup = muscleGroup;}

    public String getEquipment() {return equipment;}

    public void setEquipment(String equipment) {this.equipment = equipment;}

    public String getLevel() {return level;}

    public void setLevel(String level) {this.level = level;}

    public void copy(Lift liftChanges){
        this.title = liftChanges.getTitle();
        this.description = liftChanges.getDescription();
        this.type = liftChanges.getType();
        this.muscleGroup = liftChanges.getMuscleGroup();
        this.equipment = liftChanges.getEquipment();
        this.level = liftChanges.getLevel();
    }
}
