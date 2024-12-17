package coms309.workout.Users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import coms309.workout.CompletedWorkout.CompletedWorkout;
import coms309.workout.Role.Role;
import jakarta.persistence.*;
import coms309.workout.Profile.Profile;

import java.time.LocalDate;
import java.util.*;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "users")
public class User
{
    //name that the user signs in with
    @Id
    private String username;

    //password that the user signs in with
    @NotNull
    @Column
    private String password;

    //email that the user signs up with
    @NotNull
    @Column
    private String email;


    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="friends_with",
            joinColumns={@JoinColumn(name="user_username", referencedColumnName = "username")},
            inverseJoinColumns={@JoinColumn(name="friend_username", referencedColumnName = "username")})
    @JsonIgnore
    private Set<User> friends = new HashSet<User>();

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name="followers",
            joinColumns={@JoinColumn(name="following_username")},
            inverseJoinColumns={@JoinColumn(name="follower_username")})
    @JsonIgnore
    private Set<User> followers = new HashSet<User>();

    @ManyToMany(mappedBy = "followers", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private Set<User> following = new HashSet<>();

    @OneToOne(mappedBy= "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CompletedWorkout> completedWorkouts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private int strikes;

    public User(String username, String password, String email)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profile = new Profile();
        this.role = Role.USER;
        this.strikes = 0;
    }

    //No arg constructor not to be used.
    public User() {
    }
//    @JsonProperty("username") // This ensures only the username is serialized
    public String getUsername() {return username;}
//    @JsonIgnoreProperties({"password", "email", "friends", "following", "followers", "profile", "completedWorkouts"})
    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public Set<User> getFriends() {
        return this.friends;
    }

    public Set<User> getFollowers() {
        return this.followers;
    }

    public Set<User> getFollowing() {
        return this.following;
    }

    public Role getRole() { return this.role; }

    public int getStrikes() { return this.strikes; }

    public void setStrikes(int temp ) { this.strikes = temp; }

    public void setRole(Role temp) { this.role = temp; }

    public boolean isFriend(User other_user){
        Set<User> user1_followers = this.getFollowers();
        Set<User> user2_followers = other_user.getFollowers();
        return (user1_followers.contains(other_user) && user2_followers.contains(this));
    }

    public boolean isFollowing(User other_user){
        return (other_user.getFollowers().contains(this));
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setProfileandUser(Profile profile) {
        this.profile = profile;
        if(profile!=null){
            profile.setUser(this);
        }
    }

    public List<CompletedWorkout> getCompletedWorkouts() {
        try {
            if (this.completedWorkouts == null) {
                return null;
            } else {
                completedWorkouts.sort(Comparator.comparing(CompletedWorkout::getDate));
            }
            return completedWorkouts;
        }catch(Exception e){
            return null;
        }
    }

    public List<CompletedWorkout> getCompletedWorkoutsBetweenDates(LocalDate from, LocalDate to) {
        List<CompletedWorkout> completedWorkouts = this.getCompletedWorkouts();
        List<CompletedWorkout> toreturn = new ArrayList<>();

        for(int i = 0; i<completedWorkouts.size(); i++) {
            if(from.isBefore(completedWorkouts.get(i).getDate())&&to.isAfter(completedWorkouts.get(i).getDate())) {
                toreturn.add(completedWorkouts.get(i));
            }
        }
        return toreturn;
    }
}

