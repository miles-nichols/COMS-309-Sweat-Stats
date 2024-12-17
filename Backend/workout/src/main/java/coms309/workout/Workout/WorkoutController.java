package coms309.workout.Workout;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import coms309.workout.Lifts.Lift;

import coms309.workout.Lifts.LiftRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
class WorkoutController
{
    @Autowired
    private WorkoutRepository workoutRepository;
    @Autowired
    private LiftRepository liftRepository;

    @Operation(summary = "Get all workouts")
    @GetMapping(path = "/workouts")
    public List<Workout> getAllWorkouts()
    {
        return workoutRepository.findAll();
    }


//    @GetMapping(path = "/workout/{id}")
//    public Map<String, Object> getWorkout(@PathVariable int id)
//    {
//        Map<String, Object> result = new HashMap<>();
//        Workout workout = workoutRepository.findById(id);
//        if(workout!= null) {
//            result.put("workout", workout); //Return the workout
//            result.put("status", "200");    //Return the 200 status code "OK"
//        } else {
//            result.put("message", "Workout not found with id: " + id);     //Return the error message
//            result.put("status", "404");    //Return the 404 Not found code
//        }
//        return result;
//    }

    @Operation(summary = "Get workout by name")
    @GetMapping(path = "/workout/{name}")
    public Map<String, Object> getWorkoutByName(@PathVariable String name)
    {
        Map<String, Object> result = new HashMap<>();
        Workout workout = workoutRepository.findByName(name);
        if(workout!= null) {
            result.put("workout", workout);
            result.put("status", "200");
        } else {
            result.put("message", "Workout not found with name: " + name);
            result.put("status", "404");
        }
        return result;
    }

    @Operation(summary = "Post a new workout")
    @PostMapping("/workout")
    public @ResponseBody Map<String, String> createNewWorkout(@RequestBody Workout workout)
    {
        Map<String, String> result = new HashMap<>();
        Workout existingWorkout = workoutRepository.findByName(workout.getName());
        if(existingWorkout != null) {
            result.put("message", "Workout already exists");
            result.put("status", "409");
        } else {
            workoutRepository.save(workout);
            result.put("message", "Creating new workout");
            result.put("status", "200");
        }
        return result;
    }

    @Operation(summary = "Add lift to workout")
    @PostMapping(path = "/workout/{workoutName}")
    public @ResponseBody Map<String, String> addLiftToWorkout(@PathVariable String workoutName, @RequestParam String liftName)
    {
        Workout workout = workoutRepository.findByName(workoutName);
        Map<String, String> result = new HashMap<>();
        try
        {
            if(workout != null) {
                result.put("message", "Added lift to workout");
                result.put("status", "200");
                Lift lift = liftRepository.findByTitle(liftName);
                workout.getLifts().add(lift);
                workoutRepository.save(workout);
            } else {
                result.put("message", "Workout not found with name: " + workoutName);
                result.put("status", "404");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Or use a logger
            result.put("message", "An error occurred: " + e.getMessage());
            result.put("status", "500");
        }
        return result;
    }

//    @PutMapping("/workout/{id}")
//    public @ResponseBody Map<String, String> updateWorkout(@PathVariable int id, @RequestBody Workout newWorkout)
//    {
//        Workout oldWorkout = workoutRepository.findById(id);
//        Map<String, String> result = new HashMap<>();
//        if(oldWorkout != null) {
//            result.put("message", "Workout was updated");
//            result.put("status", "200");
//            oldWorkout.copy(newWorkout);
//            workoutRepository.save(oldWorkout);
//        } else {
//            result.put("message", "Lift not found with id: " + id);
//            result.put("status", "404");
//        }
//        return result;
//    }

    @Operation(summary = "Update workout name")
    @PutMapping("/workout/{name}")
    public @ResponseBody Map <String, String> updateWorkoutName(@PathVariable String name, @RequestParam String newName)
    {
        Workout workout = workoutRepository.findByName(name);
        Map<String, String> result = new HashMap<>();
        if(workout != null) {
            workout.setName(newName);
            workoutRepository.save(workout);
            result.put("status", "200");
            result.put("message", "Workout updated");
        } else {
            result.put("message", "Workout not found with name: " + name);
            result.put("status", "404");
        }
        return result;
    }

    @Operation(summary = "Update workout description")
    @PutMapping("/workout/{name}/description")
    public Map<String, String> updateWorkoutDescription(@PathVariable String name, @RequestParam String description)
    {
        Workout workout = workoutRepository.findByName(name);
        Map<String, String> response = new HashMap<>();
        if(workout != null) {
            workout.setDescription(description);
            workoutRepository.save(workout);
            response.put("status", "200");
            response.put("message", "Workout updated");
        } else {
            response.put("message", "Workout not found with name: " + name);
            response.put("status", "404");
        }
        return response;
    }

//    @Operation(summary = "Delete Workout by Id")
//    @DeleteMapping(path = "/workout/{id}")
//    public Map<String, String> deleteWorkout(@PathVariable int id)
//    {
//        Workout oldWorkout = workoutRepository.findById(id);
//        Map<String, String> result = new HashMap<>();
//        if(oldWorkout != null) {
//            result.put("message", "Workout was deleted");
//            result.put("status", "200");
//            workoutRepository.deleteById(id);
//        } else {
//            result.put("message", "Workout not found with id: " + id);
//            result.put("status", "404");
//        }
//        return result;
//    }

    @Operation(summary = "Delete Workout by Name")
    @DeleteMapping(path = "/workout/{name}")
    public Map<String, String> deleteWorkoutByName(@PathVariable String name)
    {
        Workout oldWorkout = workoutRepository.findByName(name);
        Map<String, String> result = new HashMap<>();
        if(oldWorkout != null) {
            result.put("message", "Workout was deleted");
            result.put("status", "200");
            workoutRepository.delete(oldWorkout);
        } else {
            result.put("message", "Workout not found with name: " + name);
            result.put("status", "404");
        }
        return result;
    }

    @Operation(summary = "Delete a lift from a workout")
    @DeleteMapping("/workout/{workoutName}/lift/{liftName}")
    public Map<String, String> deleteLiftFromWorkout(@PathVariable String workoutName, @PathVariable String liftName)
    {
        Map<String, String> response = new HashMap<>();
        Workout workout = workoutRepository.findByName(workoutName);
        Lift lift = liftRepository.findByTitle(liftName);
        if(workout != null) {
            if(lift != null) {
                workout.getLifts().remove(lift);
                workoutRepository.save(workout);
                response.put("status", "200");
                response.put("message", "Lift deleted");
            } else {
                response.put("message", "Lift not found with name: " + liftName);
                response.put("status", "404");
            }
        } else {
            response.put("message", "Workout not found with name: " + workoutName);
            response.put("status", "404");
        }
        return response;
    }
}