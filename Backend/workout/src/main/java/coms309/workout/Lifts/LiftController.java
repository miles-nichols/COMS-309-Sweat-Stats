package coms309.workout.Lifts;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import coms309.workout.Users.User;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
class LiftController {

    @Autowired
    private LiftRepository liftRepository;


    @Operation(summary="Gets Lift by Id")
    @GetMapping(path = "/lift/{id}")
    public Map<String, Object> getLift(@PathVariable int id) {
        Map<String,Object> response = new HashMap<>();

        Lift existingLift = liftRepository.findById(id);

        if (existingLift!=null) {

            // Return the student information and HTTP status code
            response.put("lift", existingLift);
            response.put("status", "200"); // HTTP 200 OK
        } else {
            // If student does not exist, return a 404 response
            response.put("message", "Lift not found with id: " + id);
            response.put("status", "404"); // HTTP 404 Not Found
        }

        // Return the JSON response
        return response;
    }

    @Operation(summary="Gets All Lifts")
    @GetMapping(path = "/lift")
    public List<Lift> getAllLifts() {
        return liftRepository.findAll();
    }

    @Operation(summary="Gets Lift by Title")
    @GetMapping("/lift/title/{title}")
    public List<Lift> getLiftByTitle(@PathVariable String title) {
        List<Lift> l = new ArrayList<>();
        l.add(liftRepository.findByTitle(title));
        return l;
    }
    @Operation(summary="Gets Lift by Equipment")
    @GetMapping("/lift/equipment/{equipment}")
    public List<Lift> getLiftByEquipment(@PathVariable String equipment) {
        return liftRepository.findByEquipment(equipment);
    }
    @Operation(summary="Gets Lift by Muscle Group")
    @GetMapping("/lift/muscleGroup/{muscleGroup}")
    public List<Lift> getLiftByMuscleGroup(@PathVariable String muscleGroup) {
        return liftRepository.findByMuscleGroup(muscleGroup);
    }

    @Operation(summary="Gets Lift by Type")
    @GetMapping("/lift/type/{type}")
    public List<Lift> getLiftByType(@PathVariable String type) {
        return liftRepository.findByType(type);
    }

    @Operation(summary="Creates new lift")
    @PostMapping("/lift")
    public @ResponseBody Map<String, String> createNewLift(@RequestBody Lift lift) {

        Map<String,String> response = new HashMap<>();
        Lift existingLift = liftRepository.findByTitle(lift.getTitle());

        if (existingLift != null) {
            // If the student already exists, return a message indicating the same
            response.put("message", "Lift already exists");
            response.put("status", "409"); // 409 Conflict HTTP status
        }else{
            // Save the new student
            liftRepository.save(lift);
            // Return success message
            response.put("message", "New lift posted correctly");
            response.put("status", "200"); // 200 OK HTTP status
        }

        // Return the JSON response
        return response;

    }

    @Operation(summary="Changes Existing Lift")
    @PutMapping("/lift/{id}")
    public @ResponseBody Map<String, String> updateLift(@PathVariable int id, @RequestBody Lift newLift) {
        Lift oldLift = liftRepository.findById(id);
        Map<String, String> response = new HashMap<>();
        if(oldLift!=null) {
            response.put("message", "Lift was updated successfully" );
            response.put("status", "200");
            oldLift.copy(newLift);
            liftRepository.save(oldLift);
        }else{
            response.put("message", "Lift not found with id: " + id);
            response.put("status", "404");
        }


        return response;
    }

    @Operation(summary="Updates Lift by Title")
    @PutMapping("/lift/title/{title}")
    public @ResponseBody Map<String, String> updateLiftByTitle(@PathVariable String title, @RequestBody Lift newLift) {
        Lift oldLift = liftRepository.findByTitle(title);
        Map<String, String> response = new HashMap<>();
        if(oldLift!=null) {
            response.put("message", "Lift was updated successfully" );
            response.put("status", "200");
            oldLift.copy(newLift);
            liftRepository.save(oldLift);
        }else{
            response.put("message", "Lift not found with title: " + title);
            response.put("status", "404");
        }


        return response;
    }

    @Operation(summary="Deletes Lift by Id")
    @DeleteMapping(path = "/lift/{id}")
    Map<String, String> deleteLift(@PathVariable int id) {
        Lift oldLift = liftRepository.findById(id);
        Map<String, String> response = new HashMap<>();
        if(oldLift!=null) {
            response.put("message", "User was Deleted successfully" );
            response.put("status", "200");
            liftRepository.deleteById(id);
        }else{
            response.put("message", "Lift not found with id: " + id);
            response.put("status", "404");
        }
        return response;
    }
    @Operation(summary="Deletes Lift by Title")
    @DeleteMapping(path = "/lift/title/{title}")
    Map<String, String> deleteLiftByTitle(@PathVariable String title) {
        Lift oldLift = liftRepository.findByTitle(title);
        Map<String, String> response = new HashMap<>();
        if(oldLift!=null) {
            response.put("message", "Lift was deleted successfully" );
            response.put("status", "200");
            liftRepository.deleteByTitle(title);
        }else{
            response.put("message", "Lift not found with title: " + title);
            response.put("status", "404");
        }
        return response;
    }

    @GetMapping("/lift/test")
    public String testNewController(){
        return "CI/CD Worked even betterer";
    }

    @GetMapping(path = "/lift/search/{search_term}")
    public List<Lift> findPossibleLifts(@PathVariable String search_term) {
        return liftRepository.findByTitleContains(search_term);
    }

}