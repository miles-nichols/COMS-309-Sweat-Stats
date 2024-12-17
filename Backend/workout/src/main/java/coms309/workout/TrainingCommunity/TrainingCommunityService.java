package coms309.workout.TrainingCommunity;

import coms309.workout.CompletedWorkout.CompletedWorkout;
import coms309.workout.CompletedWorkout.CompletedWorkoutService;
import coms309.workout.Role.Role;
import coms309.workout.Users.User;
import coms309.workout.Users.UserRepository;
import coms309.workout.Workout.Workout;
import coms309.workout.Workout.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainingCommunityService
{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainingCommunityRepository trainingCommunityRepository;
    @Autowired
    private WorkoutRepository workoutRepository;
    @Autowired
    private CompletedWorkoutService completedWorkoutService;

    public List<TrainingCommunity> getAll()
    {
        return trainingCommunityRepository.findAll();
    }

    public List<TrainingCommunity> getCommunitiesByTrainer(String trainerName)
    {
        User trainer = userRepository.findByUsername(trainerName);
        return trainingCommunityRepository.findByTrainer(trainer);
    }

    public List<TrainingCommunity> getCommunitiesByTitle(String title)
    {
        return trainingCommunityRepository.findByTitle(title);
    }

    public Map<String, String> createCommunity(String trainerUsername, String communityTitle, String description)
    {
        Map<String, String> result = new HashMap<>();
        User trainer = userRepository.findByUsername(trainerUsername);
        TrainingCommunity trainingCommunity = trainingCommunityRepository.findByTrainerAndTitle(trainer, communityTitle);
        if(trainer == null) {
            result.put("message", "Trainer not found");
            result.put("status", "404");
        } else if (trainer.getRole() != Role.TRAINER) {
            result.put("message", "User is not TRAINER");
            result.put("status", "404");
        } else if (trainingCommunity != null) {
            result.put("message", "Training community already exists");
            result.put("status", "404");
        } else {
            trainingCommunity = new TrainingCommunity(trainer, communityTitle, description);
            trainingCommunityRepository.save(trainingCommunity);
            result.put("message", "Training community successfully created");
            result.put("status", "200");
        }
        return result;
    }

    public Map<String, String> joinCommunity(String username, String trainerUsername, String communityTitle)
    {
        Map<String, String> result = new HashMap<>();
        User user = userRepository.findByUsername(username);
        User trainer = userRepository.findByUsername(trainerUsername);
        TrainingCommunity trainingCommunity = trainingCommunityRepository.findByTrainerAndTitle(trainer, communityTitle);
        if (user == null) {
            result.put("message", "User not found");
            result.put("status", "404");
        } else if (trainingCommunity == null) {
            result.put("message", "Community not found");
            result.put("status", "404");
        } else {
            result.put("message", "Community joined");
            result.put("status", "200");
            trainingCommunity.addMember(user);
            trainingCommunityRepository.save(trainingCommunity);
        }
        return result;
    }

    public Map<String, String> leaveCommunity(String username, String trainerUsername, String communityTitle)
    {
        Map<String, String> result = new HashMap<>();
        User user = userRepository.findByUsername(username);
        User trainer = userRepository.findByUsername(trainerUsername);
        TrainingCommunity trainingCommunity = trainingCommunityRepository.findByTrainerAndTitle(trainer, communityTitle);
        if (user == null) {
            result.put("message", "User not found");
            result.put("status", "404");
        } else if (trainingCommunity == null) {
            result.put("message", "Community not found");
            result.put("status", "404");
        } else {
            result.put("message", "Community left");
            result.put("status", "200");
            trainingCommunity.removeMember(user);
            trainingCommunityRepository.save(trainingCommunity);
        }
        return result;
    }

    public Map<String, String> setAssignedWorkout(String trainerUsername, String communityTitle, String workoutTitle)
    {
        Map<String, String> result = new HashMap<>();
        User trainer = userRepository.findByUsername(trainerUsername);
        TrainingCommunity trainingCommunity = trainingCommunityRepository.findByTrainerAndTitle(trainer, communityTitle);
        Workout workout = workoutRepository.findByName(workoutTitle);
        if (trainer == null) {
            result.put("message", "Trainer not found");
            result.put("status", "404");
        } else if (trainingCommunity == null) {
            result.put("message", "Training community not found");
            result.put("status", "404");
        } else if (workout == null) {
            result.put("message", "Workout not found");
            result.put("status", "404");
        } else {
            result.put("message", "Workout assigned");
            result.put("status", "200");
            trainingCommunity.setRecommendedWorkout(workout);
            workoutRepository.save(workout);
        }
        return result;
    }

//    public Map<String, String> assignWorkouts(String trainerUsername, String communityTitle, String workoutTitle, LocalDate assignedDate)
//    {
//        Map<String, String> result = new HashMap<>();
//        User trainer = userRepository.findByUsername(trainerUsername);
//        TrainingCommunity trainingCommunity = trainingCommunityRepository.findByTrainerAndTitle(trainer, communityTitle);
//        Workout workout = workoutRepository.findByName(workoutTitle);
//        if (trainingCommunity == null) {
//            result.put("message", "Training community not found");
//            result.put("status", "404");
//        } else if (trainer == null || trainer.getRole() != Role.TRAINER) {
//            result.put("message", "Trainer not found");
//            result.put("status", "404");
//        } else if (workout == null) {
//            result.put("message", "Workout not found");
//            result.put("status", "404");
//        } else {
//            for (User user : trainingCommunity.getMembers()) {
//                String completedWorkoutName = workoutTitle + LocalDateTime.now();
//                CompletedWorkout temp = completedWorkoutService.createCompletedWorkoutFromWorkoutWithDate(completedWorkoutName, user, workout, assignedDate);
//                result.put("Message", "Workouts were assigned");
//                result.put("status", "200");
//                //System.out.println("Test");
//            }
//        }
//        return result;
//    }
}
