package coms309.workout.Profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import coms309.workout.Lifts.Lift;
import coms309.workout.Users.User;
import jakarta.persistence.*;
import coms309.workout.CompletedWorkout.CompletedWorkout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filePath;

    @Column
    private String name;

    @Column
    private String bio;

    @Column
    private String favLiftName;

    @Column
    private double max_bench;

    @Column
    private double max_dead;

    @Column
    private double max_squat;

    @Column
    private double max_clean;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Profile() {
//        this.filePath = "/home/mattmn/profile_pictures/default_profile_pic.jpg";
        this.filePath = "/target/profile_pictures/default_profile_pic.jpg";
    }

    public Profile(String n, String bio, String favLift, double max_bench, double max_dead, double max_squat, double max_clean) {
//        this.filePath = "/home/mattmn/profile_pictures/default_profile_pic.jpg";
        this.filePath = "/target/profile_pictures/default_profile_pic.jpg";
        this.name = n;
        this.bio = bio;
        this.favLiftName = favLift;
        this.max_bench = max_bench;
        this.max_dead = max_dead;
        this.max_squat = max_squat;
        this.max_clean = max_clean;
    }

    public double getMax_bench() {
        return max_bench;
    }
    public void setMax_bench(double max_bench) {
        this.max_bench = max_bench;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFavLiftName() {
        return favLiftName;
    }

    public void setFavLiftName(String favLiftName) {
        this.favLiftName = favLiftName;
    }

    public double getMax_dead() {
        return max_dead;
    }

    public void setMax_dead(double max_dead) {
        this.max_dead = max_dead;
    }

    public double getMax_squat() {
        return max_squat;
    }

    public void setMax_squat(double max_squat) {
        this.max_squat = max_squat;
    }

    public double getMax_clean() {
        return max_clean;
    }

    public void setMax_clean(double max_clean) {
        this.max_clean = max_clean;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public CompletedWorkout getLastWorkout(){
        User u = this.user;
        List<CompletedWorkout> workouts = user.getCompletedWorkouts();
        if (workouts.size()==0 ){
            return null;
        }else {
            return workouts.get(workouts.size()-1);
        }
    }
//to test
    public int getStreak(){
        int streak = 1;
        User u = this.user;
        if (u.getCompletedWorkouts().size()<2){
            return u.getCompletedWorkouts().size();
        }else {
            List<CompletedWorkout> all_workouts = u.getCompletedWorkouts();
            List<CompletedWorkout> workouts = new ArrayList<CompletedWorkout>();
            for(CompletedWorkout w: all_workouts){
                if(!(w.getDate().isAfter(LocalDate.now()))){
                    workouts.add(w);
                }
            }
            CompletedWorkout iworkout = workouts.get(workouts.size() - 1);
            //if most recent workout was today or yesturday
            Collections.reverse(workouts);
            if (!(iworkout.getDate().equals(LocalDate.now().minusDays(1)) || iworkout.getDate().equals(LocalDate.now()))) {

                return 0;
            }else {
                for (CompletedWorkout w : workouts) {
                    if (iworkout.getDate().minusDays(1).equals(w.getDate())) {
                        streak++;
                        iworkout = w;
                    }else if(iworkout.getDate().equals(w.getDate())){
                        iworkout = w;
                    }
                }
                return streak;
            }
        }
    }

    public void copyProfile(Profile p){
        if(p.name!=null) {
            this.name = p.name;
        }
        if(p.bio!=null) {
            this.bio = p.bio;
        }
        if(p.favLiftName!=null) {
            this.favLiftName = p.favLiftName;
        }
        if(p.user!=null) {
            this.user = p.user;
        }
        if(p.bio!=null) {
            this.bio = p.bio;
        }
        if(p.max_bench>0){
            this.max_bench = p.max_bench;
        }
        if(p.max_squat>0){
            this.max_squat = p.max_squat;
        }
        if(p.max_clean>0){
            this.max_clean = p.max_clean;
        }
        if(p.max_dead>0){
            this.max_dead = p.max_dead;
        }
    }

}

