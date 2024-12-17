package coms309.workout.Profile;

import coms309.workout.Users.User;
import coms309.workout.Users.UserRepository;
import coms309.workout.CompletedWorkout.CompletedWorkout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    public Profile getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        return user.getProfile();
    }

    public Profile createProfile(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }

        Profile profile = new Profile();
        user.setProfile(profile);
        userRepository.save(user);
        return profile;
    }

    public Profile updateProfile(String username, Profile updateProfile) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }

        Profile existingProfile = user.getProfile();
        if (existingProfile == null) {
            existingProfile = new Profile();
            user.setProfile(existingProfile);
        }

        existingProfile.copyProfile(updateProfile);
        userRepository.save(user);
        return existingProfile;
    }

    public int getWorkoutStreak(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }

        Profile profile = user.getProfile();
        return profile.getStreak();
    }

    public CompletedWorkout getLastWorkout(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }

        Profile profile = user.getProfile();
        return profile.getLastWorkout();
    }

    public Map<String, Object> getUserLiftMax(String username, String liftTitle) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }

        Profile profile = user.getProfile();
        Map<String, Object> maxInfo = new HashMap<>();

        switch (liftTitle.toLowerCase()) {
            case "bench":
                maxInfo.put("max", profile.getMax_bench());
                break;
            case "deadlift":
                maxInfo.put("max", profile.getMax_dead());
                break;
            case "squat":
                maxInfo.put("max", profile.getMax_squat());
                break;
            case "clean":
                maxInfo.put("max", profile.getMax_clean());
                break;
            default:
                throw new IllegalArgumentException("Invalid lift title: " + liftTitle);
        }

        return maxInfo;
    }
}