package coms309.workout.TrainingCommunity;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class TrainingCommunityController
{


    private final TrainingCommunityRepository trainingCommunityRepository;
    private final TrainingCommunityService trainingCommunityService;

    public TrainingCommunityController(TrainingCommunityRepository trainingCommunityRepository, TrainingCommunityService trainingCommunityService) {
        this.trainingCommunityRepository = trainingCommunityRepository;
        this.trainingCommunityService = trainingCommunityService;
    }

    @Operation(summary = "Get all Training Communities")
    @GetMapping("/trainingCommunity")
    public List<TrainingCommunity> getAllTrainingCommunity()
    {
        return trainingCommunityRepository.findAll();
    }

    @Operation(summary = "Get Training Communities by Trainer")
    @GetMapping("/trainingCommunity/trainer/{trainerUsername}")
    public List<TrainingCommunity> getCommunitiesByTrainer(@PathVariable String trainerUsername)
    {
        return trainingCommunityService.getCommunitiesByTrainer(trainerUsername);
    }

    @Operation(summary = "Get Training Communities by Title")
    @GetMapping("/trainingCommunity/{communityTitle}")
    public List<TrainingCommunity> getCommunitiesByTitle(@PathVariable String communityTitle)
    {
        return trainingCommunityService.getCommunitiesByTitle(communityTitle);
    }

    @Operation(summary = "Create Training Community")
    @PostMapping("/trainingCommunity")
    public Map<String, String> createCommunity(@RequestParam String trainerUsername, @RequestParam String communityTitle, @RequestParam String description)
    {
        return trainingCommunityService.createCommunity(trainerUsername, communityTitle, description);
    }

    @Operation(summary = "Join a Training Community")
    @PutMapping("/trainingCommunity/{username}")
    public Map<String, String> joinCommunity(@PathVariable String username, @RequestParam String trainerUsername, @RequestParam String communityTitle)
    {
        return trainingCommunityService.joinCommunity(username, trainerUsername, communityTitle);
    }

    @Operation(summary = "Leave a Training Community")
    @DeleteMapping("/trainingCommunity/{username}")
    public Map<String, String> leaveCommunity(@PathVariable String username, @RequestParam String trainerUsername, @RequestParam String communityTitle)
    {
        return trainingCommunityService.leaveCommunity(username, trainerUsername, communityTitle);
    }

    @Operation(summary = "Assign Workouts to Users in the Community")
    @PostMapping("/trainingCommunity/workout/assign")
    public Map<String, String> assignWorkouts(@RequestParam String trainerUsername, @RequestParam String communityTitle, @RequestParam String workoutTitle)
    {
        return trainingCommunityService.setAssignedWorkout(trainerUsername, communityTitle, workoutTitle);
    }
}
