package coms309.workout.CompletedWorkout;

import coms309.workout.LiftSet.LiftSet;
import coms309.workout.Lifts.Lift;
import coms309.workout.Lifts.LiftRepository;
import coms309.workout.Set.Set;
import coms309.workout.Users.User;
import coms309.workout.Users.UserRepository;
import coms309.workout.Workout.Workout;
import coms309.workout.Workout.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CompletedWorkoutService {
    @Autowired
    private CompletedWorkoutRepository completedWorkoutRepository;
    @Autowired
    private WorkoutRepository workoutRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LiftRepository liftRepository;

    public List<CompletedWorkout> getAllCompletedWorkouts() {
        return completedWorkoutRepository.findAll();
    }

    public List<CompletedWorkout> getCompletedWorkoutsForUser(String username) {
        return completedWorkoutRepository.findByUserUsernameOrderByDateAsc(username);
    }

    public Map<String, Object> getCompletedWorkoutById(int id) {
        Map<String, Object> response = new HashMap<>();
        CompletedWorkout completedWorkout = completedWorkoutRepository.findById(id);
        if (completedWorkout != null) {
            response.put("completedWorkout", completedWorkout);
            response.put("status", "200");
        } else {
            response.put("message", "CompletedWorkout not found with id: " + id);
            response.put("status", "404");
        }
        return response;
    }

    public Map<String, String> createEmptyCompletedWorkout(String name, String username) {
        Map<String, String> response = new HashMap<>();
        CompletedWorkout completedWorkout = completedWorkoutRepository.findByName(name);
        User user = userRepository.findByUsername(username);

        if (user != null) {
            if (completedWorkout == null) {
                completedWorkout = new CompletedWorkout(name, user);
                user.getCompletedWorkouts().add(completedWorkout);
                userRepository.save(user);
                response.put("status", "200");
                response.put("message", "Empty CompletedWorkout created");
            } else {
                response.put("status", "404");
                response.put("message", "CompletedWorkout already exists with name: " + name);
            }
        } else {
            response.put("status", "404");
            response.put("message", "User not found with username: " + username);
        }
        return response;
    }

    public Map<String, String> createCompletedWorkoutFromTemplate(int workoutId, String name, String username) {
        Map<String, String> response = new HashMap<>();
        Workout workout = workoutRepository.findById(workoutId);
        CompletedWorkout completedWorkout = completedWorkoutRepository.findByName(name);
        User user = userRepository.findByUsername(username);

        if (workout != null && user != null) {
            if (completedWorkout == null) {
                completedWorkout = createCompletedWorkoutFromWorkout(name, user, workout);
                response.put("status", "200");
                response.put("message", "New CompletedWorkout created successfully");
            } else {
                response.put("status", "404");
                response.put("message", "CompletedWorkout already exists with name: " + name);
            }
        } else {
            handleWorkoutOrUserNotFound(response, workout, workoutId, username);
        }

        return response;
    }

    public Map<String, String> createCompletedWorkoutFromTemplateByName(String workoutName, String name, String username) {
        Map<String, String> response = new HashMap<>();
        Workout workout = workoutRepository.findByName(workoutName);
        CompletedWorkout completedWorkout = completedWorkoutRepository.findByName(name);
        User user = userRepository.findByUsername(username);

        if (workout != null && user != null) {
            if (completedWorkout == null) {
                completedWorkout = createCompletedWorkoutFromWorkout(name, user, workout);
                response.put("status", "200");
                response.put("message", "New CompletedWorkout created successfully");
            } else {
                response.put("status", "404");
                response.put("message", "CompletedWorkout already exists with name: " + name);
            }
        } else {
            handleWorkoutOrUserNotFoundByName(response, workout, workoutName, username);
        }

        return response;
    }

    public Map<String, String> changeCompletedWorkoutName(String name, String newName, String username) {
        Map<String, String> response = new HashMap<>();
        CompletedWorkout completedWorkout = completedWorkoutRepository.findByName(name);
        if (completedWorkout != null) {
            completedWorkout.setName(newName);
            User user = userRepository.findByUsername(username);
            userRepository.save(user);
            response.put("status", "200");
            response.put("message", "CompletedWorkout updated successfully");
        } else {
            response.put("status", "404");
            response.put("message", "CompletedWorkout not found with name: " + name);
        }
        return response;
    }

    public Map<String, String> addLiftSetToCompletedWorkout(String name, String liftTitle, String username) {
        Map<String, String> response = new HashMap<>();
        User user = userRepository.findByUsername(username);
        CompletedWorkout completedWorkout = completedWorkoutRepository.findByNameAndUser(name, user);
        Lift lift = liftRepository.findByTitle(liftTitle);
        if (completedWorkout != null) {
            if (lift != null) {
                LiftSet liftSet = new LiftSet(lift);
                liftSet.setCompletedWorkout(completedWorkout);
                completedWorkout.getCompletedSets().add(liftSet);

                userRepository.save(user);
                response.put("status", "200");
                response.put("message", "LiftSet added successfully");
            } else {
                response.put("status", "404");
                response.put("message", "Lift not found with title: " + liftTitle);
            }
        } else {
            response.put("status", "404");
            response.put("message", "CompletedWorkout not found with name: " + name);
        }
        return response;
    }
    public Map<String, String> addLiftSetToCompletedWorkoutByDate(String name, String liftTitle, String username, String date){
        Map<String, String> response = new HashMap<>();
        User user = userRepository.findByUsername(username);
        LocalDate d = LocalDate.parse(date);
        CompletedWorkout completedWorkout = completedWorkoutRepository.findByNameAndUserAndDate(name, user, d);
        Lift lift = liftRepository.findByTitle(liftTitle);
        if (completedWorkout != null) {
            if (lift != null) {
                LiftSet liftSet = new LiftSet(lift);
                liftSet.setCompletedWorkout(completedWorkout);
                completedWorkout.getCompletedSets().add(liftSet);

                userRepository.save(user);
                response.put("status", "200");
                response.put("message", "LiftSet added successfully");
            } else {
                response.put("status", "404");
                response.put("message", "Lift not found with title: " + liftTitle);
            }
        } else {
            response.put("status", "404");
            response.put("message", "CompletedWorkout not found with name: " + name);
        }
        return response;
    }
    public Map<String, String> addSetToLiftSet(String name, String liftName, Set set, String username) {
        Map<String, String> response = new HashMap<>();
        CompletedWorkout completedWorkout = completedWorkoutRepository.findByName(name);
        if (completedWorkout != null) {
            for (LiftSet liftSet : completedWorkout.getCompletedSets()) {
                if (liftSet.getLift().getTitle().equals(liftName)) {
                    liftSet.addSet(set);
                    User user = userRepository.findByUsername(username);
                    userRepository.save(user);
                    response.put("status", "200");
                    response.put("message", "Set added to " + liftName + " successfully");
                    return response;
                }
            }
            response.put("status", "404");
            response.put("message", "LiftSet not found with title: " + liftName);
        } else {
            response.put("status", "404");
            response.put("message", "CompletedWorkout not found with name: " + name);
        }
        return response;
    }

    public Map<String, String> deleteCompletedWorkout(int id) {
        Map<String, String> response = new HashMap<>();
        CompletedWorkout completedWorkout = completedWorkoutRepository.findById(id);
        if (completedWorkout != null) {
            completedWorkoutRepository.delete(completedWorkout);
            response.put("status", "200");
            response.put("message", "Completed Workout deleted successfully");
        } else {
            response.put("status", "404");
            response.put("message", "CompletedWorkout not found with id: " + id);
        }
        return response;
    }

    public Map<String, String> deleteCompletedWorkoutByName(String name) {
        Map<String, String> response = new HashMap<>();
        CompletedWorkout completedWorkout = completedWorkoutRepository.findByName(name);
        if (completedWorkout != null) {
            completedWorkoutRepository.delete(completedWorkout);
            response.put("status", "200");
            response.put("message", "Completed Workout deleted successfully");
        } else {
            response.put("status", "404");
            response.put("message", "CompletedWorkout not found with name: " + name);
        }
        return response;
    }

    public Map<String, String> deleteSetFromLiftSet(String name, String liftName, int setNum) {
        Map<String, String> response = new HashMap<>();
        CompletedWorkout completedWorkout = completedWorkoutRepository.findByName(name);
        if (completedWorkout != null) {
            for (LiftSet liftSet : completedWorkout.getCompletedSets()) {
                if (liftSet.getLift().getTitle().equals(liftName)) {
                    liftSet.getSets().remove(setNum - 1);
                    completedWorkoutRepository.save(completedWorkout);
                    response.put("status", "200");
                    response.put("message", "Set deleted from LiftSet successfully");
                    return response;
                }
            }
            response.put("status", "404");
            response.put("message", "LiftSet not found with title: " + liftName);
        } else {
            response.put("status", "404");
            response.put("message", "CompletedWorkout not found with name: " + name);
        }
        return response;
    }

    public Map<String, Object> getUserWorkoutsForDate(String username, String date) {
        LocalDate d = LocalDate.parse(date);
        Map<String, Object> response = new HashMap<>();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            response.put("status", "404");
            response.put("message", "User not found");
            response.put("workouts", null);
        } else {
            List<CompletedWorkout> workouts = completedWorkoutRepository.findByUserUsernameAndDate(username, d);
            if (workouts == null || workouts.isEmpty()) {
                response.put("status", "200");
                response.put("message", "No workouts found");
            } else {
                response.put("message", "Workouts retrieved successfully");
                response.put("status", "200");
            }
            response.put("Workouts", workouts);
        }
        response.put("dow", d.getDayOfWeek());
        return response;
    }

    public Map<String, String> createCompletedWorkoutFromTemplateWithDate(String workoutName, String name, String username, String date) {
        LocalDate d = LocalDate.parse(date);
        Map<String, String> response = new HashMap<>();
        Workout workout = workoutRepository.findByName(workoutName);
        CompletedWorkout completedWorkout = completedWorkoutRepository.findByName(name);
        User user = userRepository.findByUsername(username);

        if (workout != null && user != null) {
            if (completedWorkout == null) {
                completedWorkout = createCompletedWorkoutFromWorkoutWithDate(name, user, workout, d);
                response.put("status", "200");
                response.put("message", "New CompletedWorkout created successfully");
            } else {
                response.put("status", "404");
                response.put("message", "CompletedWorkout already exists with name: " + name);
            }
        } else {
            handleWorkoutOrUserNotFoundByName(response, workout, workoutName, username);
        }

        return response;
    }


    // Helper methods
    private CompletedWorkout createCompletedWorkoutFromWorkout(String name, User user, Workout workout) {
        CompletedWorkout completedWorkout = new CompletedWorkout(name, user);
        java.util.Set<Lift> lifts = workout.getLifts();

        for (Lift lift : lifts) {
            Lift temp = liftRepository.findByTitle(lift.getTitle());
            LiftSet liftSet = new LiftSet(temp);
            liftSet.setCompletedWorkout(completedWorkout);
            completedWorkout.getCompletedSets().add(liftSet);
        }
        user.getCompletedWorkouts().add(completedWorkout);
        userRepository.save(user);

        return completedWorkout;
    }

    public CompletedWorkout createCompletedWorkoutFromWorkoutWithDate(String name, User user, Workout workout, LocalDate date) {
        CompletedWorkout completedWorkout = new CompletedWorkout(name, user, date);
        java.util.Set<Lift> lifts = workout.getLifts();

        for (Lift lift : lifts) {
            Lift temp = liftRepository.findByTitle(lift.getTitle());
            LiftSet liftSet = new LiftSet(temp);
            liftSet.setCompletedWorkout(completedWorkout);
            completedWorkout.getCompletedSets().add(liftSet);
        }
        user.getCompletedWorkouts().add(completedWorkout);
        userRepository.save(user);

        return completedWorkout;
    }



    private void handleWorkoutOrUserNotFound(Map<String, String> response, Workout workout, int workoutId, String username) {
        if (workout == null) {
            response.put("status", "404");
            response.put("message", "Workout not found with id: " + workoutId);
        } else {
            response.put("status", "404");
            response.put("message", "User not found with username: " + username);
        }
    }

    private void handleWorkoutOrUserNotFoundByName(Map<String, String> response, Workout workout, String workoutName, String username) {
        if (workout == null) {
            response.put("status", "404");
            response.put("message", "Workout not found with name: " + workoutName);
        } else {
            response.put("status", "404");
            response.put("message", "User not found with username: " + username);
        }
    }

    public Map<String, String> createCompletedWorkoutWithoutTemplateWithDate(String name, String username, String date){
        Map<String, String> response = new HashMap<>();
        User user = userRepository.findByUsername(username);
        if(user!=null){
            LocalDate d = LocalDate.parse(date);
            CompletedWorkout completedWorkout = new CompletedWorkout(name,user, d);
            user.getCompletedWorkouts().add(completedWorkout);
            userRepository.save(user);
            completedWorkoutRepository.save(completedWorkout);
            response.put("status", "200");
            response.put("message", "CompletedWorkout created successfully");
        }else{
            response.put("status", "404");
            response.put("message", "User not found with username: " + username);
        }
        return response;
    };

}