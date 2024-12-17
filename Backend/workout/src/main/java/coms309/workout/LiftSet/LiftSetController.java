package coms309.workout.LiftSet;

import coms309.workout.Lifts.Lift;
import coms309.workout.Lifts.LiftRepository;
import coms309.workout.Set.Set;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LiftSetController
{
    @Autowired
    private LiftSetRepository liftSetRepository;
    @Autowired
    private LiftRepository liftRepository;

    @Operation(summary = "Displays all curent LiftSet objects")
    @GetMapping("/liftSet")
    public List<LiftSet> getAllLiftSets()
    {
        return liftSetRepository.findAll();
    }

//    @Operation(summary = "Create new LiftSet object to store set data for Completed Workout")
//    @PostMapping("/liftSet/{id}")
//    public @ResponseBody Map<String, String> createLiftSetByLiftId(@PathVariable int id)
//    {
//        Map<String, String> response = new HashMap<>();
//        Lift lift = liftRepository.findById(id);
//        LiftSet existingLiftSet = liftSetRepository.findByLift(lift);
//
//        if (existingLiftSet != null) {
//            response.put("message", "Set data for lift already exists");
//            response.put("status", "409");
//        } else {
//            liftSetRepository.save(new LiftSet(lift));
//            response.put("message", "Set data for lift created");
//            response.put("status", "200");
//        }
//        return response;
//    }

    @Operation(summary = "Add Set to LiftSet")
    @PutMapping("/liftSet/{LiftSetid}")
    public @ResponseBody Map<String, String> addSetToLiftSet(@PathVariable int LiftSetid, @RequestBody Set set)
    {
        Map<String, String> response = new HashMap<>();
        LiftSet existingLiftSet = liftSetRepository.findById(LiftSetid);
        if (existingLiftSet != null) {
            existingLiftSet.addSet(set);
            liftSetRepository.save(existingLiftSet);
            response.put("message", "Set data for lift added");
            response.put("status", "200");
        } else {
            response.put("message", "LiftSet not found with id: " + LiftSetid);
            response.put("status", "404");
        }
        return response;
    }
}