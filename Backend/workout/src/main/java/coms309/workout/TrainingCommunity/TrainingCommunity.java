package coms309.workout.TrainingCommunity;

import coms309.workout.Users.User;
import coms309.workout.Workout.Workout;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Training_Community")
public class TrainingCommunity
{
    @Id
    @GeneratedValue
    private int id;

    @Column
    private String title;

    @ManyToOne
    @JoinColumn(name = "Trainer")
    private User trainer;

    @ManyToMany
    @JoinTable(name = "Community_User",
                joinColumns = { @JoinColumn(name="Community_ID") },
                inverseJoinColumns = { @JoinColumn(name="User_ID")})
    private Set<User> members = new HashSet<>();

    @ManyToOne
    @JoinColumn
    private Workout recommendedWorkout;

    @Column
    private String description;

    public TrainingCommunity() { }

    public TrainingCommunity(User trainer, String title, String description)
    {
        this.trainer = trainer;
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public User getTrainer() { return trainer; }

    public Set<User>  getMembers() { return members; }

    public void addMember(User user) { this.members.add(user); }

    public void removeMember(User user) { members.remove(user); }

    public Workout getRecommendedWorkout() { return recommendedWorkout; }

    public void setRecommendedWorkout(Workout workout) { this.recommendedWorkout = workout; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}
