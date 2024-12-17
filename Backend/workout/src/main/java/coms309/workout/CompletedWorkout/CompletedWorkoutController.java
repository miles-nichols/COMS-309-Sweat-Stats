package coms309.workout.CompletedWorkout;

import coms309.workout.LiftSet.LiftSet;
import coms309.workout.Lifts.Lift;
import coms309.workout.Set.Set;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class CompletedWorkoutController {
    @Autowired
    private CompletedWorkoutService completedWorkoutService;
    @Autowired
    private CompletedWorkoutRepository completedWorkoutRepository;

    @Operation(summary = "Gets all the Completed Workout Objects in the DB")
    @GetMapping("/completedWorkout")
    public List<CompletedWorkout> getAllCompletedWorkout() {
        return completedWorkoutService.getAllCompletedWorkouts();
    }

    @Operation(summary = "Gets all the CompletedWorkout Objects tied to a given User")
    @GetMapping("/completedWorkout/user/{username}")
    public List<CompletedWorkout> getCompletedWorkoutsForUser(@PathVariable String username) {
        return completedWorkoutService.getCompletedWorkoutsForUser(username);
    }

    @Operation(summary = "Gets the Completed Workout Object with the given ID")
    @GetMapping("/completedWorkout/{id}")
    public Map<String, Object> getCompletedWorkoutById(@PathVariable int id) {
        return completedWorkoutService.getCompletedWorkoutById(id);
    }

    @Operation(summary = "Posts a new CompletedWorkout with no lifts entered")
    @PostMapping("/completedWorkout/{name}")
    public Map<String, String> postCompletedWorkout(@PathVariable String name, @RequestParam String username) {
        return completedWorkoutService.createEmptyCompletedWorkout(name, username);
    }

    @Operation(summary = "Posts a new CompletedWorkout object based a Workout template - Primary Post")
    @PostMapping("/completedWorkout/template/{name}/{workoutId}")
    public Map<String, String> postCompletedWorkoutFromTemplate(@PathVariable int workoutId, @PathVariable String name, @RequestParam String username) {
        return completedWorkoutService.createCompletedWorkoutFromTemplate(workoutId, name, username);
    }

    @Operation(summary = "Posts a new CompletedWorkout object based a Workout template - Primary Post")
    @PostMapping("/completedWorkout/{name}/workout/{workoutName}")
    public Map<String, String> postCompletedWorkoutFromTemplateByName(@PathVariable String workoutName, @PathVariable String name, @RequestParam String username) {
        return completedWorkoutService.createCompletedWorkoutFromTemplateByName(workoutName, name, username);
    }

    @Operation(summary = "Change the name of the CompletedWorkout(by name)")
    @PutMapping("/completedWorkout/changename/{name}")
    public Map<String, String> changeCompletedWorkoutNameByName(@PathVariable String name, @RequestParam String newName, @RequestParam String username) {
        return completedWorkoutService.changeCompletedWorkoutName(name, newName, username);
    }

    @Operation(summary = "Add a new LiftSet to CompletedWorkout by Lift title")
    @PutMapping("/completedWorkout/{name}/liftset/{liftTitle}")
    public Map<String, String> addLiftSetToCompletedWorkout(@PathVariable String name, @PathVariable String liftTitle, @RequestParam String username) {
        return completedWorkoutService.addLiftSetToCompletedWorkout(name, liftTitle, username);
    }

    @Operation(summary = "Add a new LiftSet to CompletedWorkout by Lift title")
    @PutMapping("/completedWorkout/{name}/liftset/{liftTitle}/{date}")
    public Map<String, String> addLiftSetToCompletedWorkout(@PathVariable String name, @PathVariable String liftTitle, @RequestParam String username, @PathVariable String date) {
        return completedWorkoutService.addLiftSetToCompletedWorkoutByDate(name, liftTitle, username, date);
    }

    @Operation(summary = "Add Set to LiftSet(by title) in a CompletedWorkout(by name)")
    @PutMapping("/completedWorkout/name/{name}")
    public Map<String, String> addSetToLiftSetInComWorkByName(@PathVariable String name, @RequestParam String LiftName, @RequestBody Set set, @RequestParam String username) {
        return completedWorkoutService.addSetToLiftSet(name, LiftName, set, username);
    }

    @Operation(summary = "Deletes a CompletedWorkout by Id")
    @DeleteMapping("/completedWorkout/{id}")
    public Map<String, String> deleteCompletedWorkout(@PathVariable int id) {
        return completedWorkoutService.deleteCompletedWorkout(id);
    }

    @Operation(summary = "Deletes a CompletedWorkout by name")
    @DeleteMapping("/completedWorkout/name/{name}")
    public Map<String, String> deleteCompletedWorkoutByName(@PathVariable String name) {
        return completedWorkoutService.deleteCompletedWorkoutByName(name);
    }

    @Operation(summary = "Deletes a Set from LiftSet based on ComWork Name and Lift Name")
    @DeleteMapping("/completedWorkout/name/{name}/{liftName}")
    public Map<String, String> deleteSetFromComWorkLiftSetByName(@PathVariable String name, @PathVariable String liftName, @RequestParam int setNum) {
        return completedWorkoutService.deleteSetFromLiftSet(name, liftName, setNum);
    }

    @Operation(summary = "Gets the workouts that a user did on a certain date, date is in YYYY-MM-DD format")
    @GetMapping(path="/calendar/{username}/{date}")
    public Map<String,Object> getUsersWorkoutsforDate(@PathVariable String username, @PathVariable String date) {
        return completedWorkoutService.getUserWorkoutsForDate(username, date);
    }

    @Operation(summary = "Posts a new CompletedWorkout object based a Workout template with specific date")
    @PostMapping("/completedWorkout/{name}/workout/{workoutName}/{date}")
    public Map<String, String> postCompletedWorkoutFromTemplateByNameWithDate(
            @PathVariable String workoutName,
            @PathVariable String name,
            @RequestParam String username,
            @PathVariable String date) {
        return completedWorkoutService.createCompletedWorkoutFromTemplateWithDate(workoutName, name, username, date);
    }

    @Operation(summary = "Posts a new CompletedWorkout object without a WorkoutTemplate with specific date")
    @PostMapping("/completedWorkout/{name}/{date}")
    public Map<String, String> postCompletedWorkoutByNameWithDate(
            @PathVariable String name,
            @RequestParam String username,
            @PathVariable String date) {
        return completedWorkoutService.createCompletedWorkoutWithoutTemplateWithDate(name, username, date);
    }

    @GetMapping(path="/completedworkout/test")
    public List<CompletedWorkout> getCompletedWorkoutsinRange() {
        return completedWorkoutRepository.getCompletedWorkoutsByUserInRange("a", "2024-12-01","2024-12-10");
    }

    @GetMapping(path="/completedworkout/lift/test")
    public List<LiftSet> getLiftsinRange() {
        return completedWorkoutRepository.getLiftsByUserInRange("a", "2024-12-01","2024-12-10");
    }

    @GetMapping(path="/completedworkout/muscleGroup/test")
    public List<String> getMuscleGroupsInRange() {
        return completedWorkoutRepository.getLiftsByUserInTwoRangeByMuscleGroup("a", "2024-12-01","2024-12-05", "2024-12-04","2024-12-10", "Chest");
    }

}