package coms309.workout.Users;

import coms309.workout.CompletedWorkout.CompletedWorkout;
import coms309.workout.CompletedWorkout.CompletedWorkoutRepository;
import coms309.workout.LiftSet.LiftSet;
import coms309.workout.LiftSet.LiftSetRepository;
import coms309.workout.Lifts.Lift;
import coms309.workout.Lifts.LiftRepository;
import coms309.workout.Profile.Profile;
import coms309.workout.Role.Role;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class UserService {
    @Autowired
    private LiftSetRepository liftSetRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LiftRepository liftRepository;
    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
    @Autowired
    private CompletedWorkoutRepository completedWorkoutRepository;

    /**
     * Get all users from the database
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Authenticate user login
     */
    public Map<String, String> authenticateUser(User user) {
        Map<String, String> result = new HashMap<>();
        User existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser != null && user.getPassword().equals(existingUser.getPassword()) && existingUser.getStrikes() < 3) {
            if (user.getProfile() == null) {
                user.setProfile(new Profile());
            }
            result.put("message", "Login successful");
            result.put("status", "200");
        } else if (existingUser != null && existingUser.getStrikes() > 2) {
            result.put("message", "Too many strikes");
            result.put("status", "401");
        } else if (existingUser != null) {
            result.put("message", "Login failed");
            result.put("status", "401");
        } else {
            result.put("message", "User not found with username: " + user.getUsername());
            result.put("status", "404");
        }
        return result;
    }

    /**
     * Create a new user
     */
    public Map<String, String> createUser(User user) {
        Map<String, String> response = new HashMap<>();
        User existingUserName = userRepository.findByUsername(user.getUsername());
        User existingUserEmail = userRepository.findByEmail(user.getEmail());

        if (existingUserName != null) {
            response.put("message", "Username already exists");
            response.put("status", "409");
        } else if (existingUserEmail != null) {
            response.put("message", "Email already exists");
            response.put("status", "409");
        } else {
            user.setProfileandUser(new Profile());
            user.setRole(Role.USER);
            userRepository.save(user);
            response.put("message", "New User posted correctly");
            response.put("status", "200");
        }
        return response;
    }

    /**
     * Update user's email
     */
    public Map<String, String> updateUserEmail(User newUser) {
        Map<String, String> response = new HashMap<>();
        User oldUser = userRepository.findByUsername(newUser.getUsername());

        if (oldUser != null) {
            oldUser.setEmail(newUser.getEmail());
            userRepository.save(oldUser);
            response.put("message", "Student email was updated successfully: " + newUser.getEmail());
            response.put("status", "200");
        } else {
            response.put("message", "User not found with username: " + newUser.getUsername());
            response.put("status", "404");
        }
        return response;
    }

    /**
     * Update user's username
     */
    public Map<String, String> updateUsername(User newUser) {
        Map<String, String> response = new HashMap<>();
        User oldUser = userRepository.findByEmail(newUser.getEmail());

        if (oldUser != null) {
            oldUser.setUsername(newUser.getUsername());
            userRepository.save(oldUser);
            response.put("message", "User username was updated successfully: " + newUser.getUsername());
            response.put("status", "200");
        } else {
            response.put("message", "User not found with email: " + newUser.getEmail());
            response.put("status", "404");
        }
        return response;
    }

    /**
     * Update user's password
     */
    public Map<String, String> updatePassword(User newUser) {
        Map<String, String> response = new HashMap<>();
        User oldUser = userRepository.findByEmailAndUsername(newUser.getEmail(), newUser.getUsername());

        if (oldUser != null) {
            oldUser.setPassword(newUser.getPassword());
            userRepository.save(oldUser);
            response.put("message", "User password was updated successfully: " + newUser.getPassword());
            response.put("status", "200");
        } else {
            response.put("message", "User not found with email: " + newUser.getEmail() + " and Username: " + newUser.getUsername());
            response.put("status", "404");
        }
        return response;
    }

    /**
     * Delete user by username
     */
    public Map<String, String> deleteUser(String username) {
        Map<String, String> response = new HashMap<>();
        User user = userRepository.findByUsername(username);

        if (user != null) {
            userRepository.deleteByUsername(username);
            response.put("message", "User deleted successfully");
            response.put("status", "200");
        } else {
            response.put("message", "User not found with username: " + username);
            response.put("status", "404");
        }
        return response;
    }

    /**
     * Search users by partial username
     */
    public List<User> searchUsers(String partUsername) {
        return userRepository.findByUsernameStartingWith(partUsername);
    }

    /**
     * Handle user following another user
     */
    public Map<String, String> followUser(String followerUsername, String followingUsername) {
        Map<String, String> response = new HashMap<>();
        User follower = userRepository.findByUsername(followerUsername);
        User following = userRepository.findByUsername(followingUsername);

        if (follower == null || following == null) {
            response.put("message", "One or both users not found");
            response.put("status", "404");
            return response;
        }

        if (following.getFollowers().contains(follower)) {
            response.put("message", followerUsername + " is already following " + followingUsername);
            response.put("status", "409");
            return response;
        }

        following.getFollowers().add(follower);
        follower.getFollowing().add(following);
        userRepository.save(following);
        userRepository.save(follower);

        if (following.isFriend(follower)) {
            following.getFriends().add(follower);
            follower.getFriends().add(following);
            userRepository.save(following);
            userRepository.save(follower);
            response.put("message", "Users are now friends");
        } else {
            response.put("message", "Follow successful");
        }
        response.put("status", "200");
        return response;
    }

    /**
     * Handle user unfollowing another user
     */
    public Map<String, String> unfollowUser(String followerUsername, String followingUsername) {
        Map<String, String> response = new HashMap<>();
        User follower = userRepository.findByUsername(followerUsername);
        User following = userRepository.findByUsername(followingUsername);

        if (follower == null || following == null) {
            response.put("message", "One or both users not found");
            response.put("status", "404");
            return response;
        }

        if (!follower.isFollowing(following)) {
            response.put("message", "User: " + follower.getUsername() + " is not following User: " + following.getUsername());
            response.put("status", "409");
            return response;
        }

        follower.getFollowing().remove(following);
        following.getFollowers().remove(follower);
        follower.getFriends().remove(following);
        following.getFriends().remove(follower);
        userRepository.save(follower);
        userRepository.save(following);

        response.put("message", "Successfully unfollowed");
        response.put("status", "200");
        return response;
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get user's friends
     */
    public Set<User> getUserFriends(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? user.getFriends() : new HashSet<>();
    }

    /**
     * Get user's followers
     */
    public Set<User> getUserFollowers(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? user.getFollowers() : new HashSet<>();
    }

    /**
     * Get users that user is following
     */
    public Set<User> getUserFollowing(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? user.getFollowing() : new HashSet<>();
    }

    /**
     * Returns specified liftSets from list of CompletedWorkouts
     */
    public List<LiftSet> getLiftFromCompletedWorkout(List<CompletedWorkout> completedWorkouts, Lift l) {
        List<LiftSet> liftSets = new ArrayList<>();
        for(CompletedWorkout w: completedWorkouts){
            liftSets.addAll(w.getLifts(l));
        }
        return liftSets;
    }

    /**
     * Returns specified liftSets from list of CompletedWorkouts
     */
    public List<LiftSet> getLiftsFromCompletedWorkout(List<CompletedWorkout> completedWorkouts, List<Lift> list) {
        List<LiftSet> liftSets = new ArrayList<>();
        for(CompletedWorkout w: completedWorkouts){
            for(Lift l: list)
                liftSets.addAll(w.getLifts(l));
        }
        return liftSets;
    }

    /**
     * Returns a map with lift names as keys and the values as a list of all sets that are done of that lift
     */
    public Map<String, List<coms309.workout.Set.Set>> filterLiftsByMuscleGroup(List<CompletedWorkout> workouts, String muscleGroup){
        List<LiftSet> hold = new ArrayList<>();
        //filter out not in muscleGroup
        HashMap<String, List<coms309.workout.Set.Set>> ret = new HashMap<>();
        for(CompletedWorkout w: workouts){
            for(LiftSet l: w.getCompletedSets()) {
                if (l.getLift().getMuscleGroup().equals(muscleGroup)) {
                    hold.add(l);
                }
            }
        }
        List<coms309.workout.Set.Set> s = new ArrayList<>();
        //put into map
        for(LiftSet l: hold) {
            if (!ret.containsKey(l.getLift().getTitle())) {
                ;
                ret.put(l.getLift().getTitle(), l.getSets());
            } else {
                ret.get(l.getLift().getTitle()).addAll(l.getSets());
            }
        }
        return ret;

    }

    /**
     * return list of lifts that are in both maps
     */
    public List<String> findCommonLifts(Map<String, List<coms309.workout.Set.Set>> t1, Map<String, List<coms309.workout.Set.Set>> t2){
        List<String> ret = new ArrayList<>();
        for(String title: t1.keySet()){
            if(t2.containsKey(title)){
                ret.add(title);
            }
        }
        for(String title: ret){
        }
        return ret;
    }

    /**
     * calculates the average One Rep Max from a list of LiftSets
     */
    public double computeAverageTopORM(List<LiftSet> ls){
        int count = 0;
        double sum = 0.0;
        for(LiftSet l: ls) {
            if (l.getSets().isEmpty()) {
                continue;
            } else {
                sum += l.findBestSet().calculateOneRepMax();
                count++;
            }
        }
        if(count<=0) {
            return -1;
        }
        return sum/count;
    }

    /**
     * calculates the average One Rep Max from a list of LiftSets
     */
    public double computeAverageOverallORM(List<coms309.workout.Set.Set> s){
        int count = 0;
        double sum = 0.0;
        for(coms309.workout.Set.Set l: s) {
            sum += l.calculateOneRepMax();
            count++;
        }
        if(count<=0) {
            return -1;
        }
        return sum/count;
    }

    /**
     * Compare a users progress on a lift over time
     */
    public Map<String, Object> compareUserLifts(String tf1s, String tf1e, String tf2s, String tf2e, String liftName, String username) {
        Map<String, Object> result = new HashMap<>();

        // Early validation with direct returns
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return createErrorResult("404", "User not found", liftName);
        }

        Lift lift = liftRepository.findByTitle(liftName);
        if (lift == null) {
            return createErrorResult("404", "Lift not found", liftName);
        }

        // Parse dates once
        LocalDate timeframeOneStart = LocalDate.parse(tf1s);
        LocalDate timeframeOneEnd = LocalDate.parse(tf1e);
        LocalDate timeframeTwoStart = LocalDate.parse(tf2s);
        LocalDate timeframeTwoEnd = LocalDate.parse(tf2e);

        // Get workouts for specific timeframes in a single method call
        List<CompletedWorkout> rangeOneWorkouts = user.getCompletedWorkoutsBetweenDates(timeframeOneStart, timeframeOneEnd);
        List<CompletedWorkout> rangeTwoWorkouts = user.getCompletedWorkoutsBetweenDates(timeframeTwoStart, timeframeTwoEnd);

        // Get lift sets efficiently
        List<LiftSet> rangeOneLiftSets = getLiftFromCompletedWorkout(rangeOneWorkouts, lift);
        List<LiftSet> rangeTwoLiftSets = getLiftFromCompletedWorkout(rangeTwoWorkouts, lift);

        // Check lift set availability with early returns
        if (rangeOneLiftSets.isEmpty()) {
            return createErrorResult("404", "User hasn't done lift in earlier timeframe", liftName);
        }

        if (rangeTwoLiftSets.isEmpty()) {
            return createErrorResult("404", "User hasn't done lift in later timeframe", liftName);
        }

        // Compute ORMs
        double firstORM = computeAverageTopORM(rangeOneLiftSets);
        double secondORM = computeAverageTopORM(rangeTwoLiftSets);

        // Validate ORM calculation
        if (firstORM < 0 || secondORM < 0) {
            return createErrorResult("404", "User hasn't done any sets of this workout", liftName);
        }

        // Compute and return progress
        double progressPercentage = (secondORM - firstORM) / firstORM;

        result.put("status", "200");
        result.put("message", "Progress recorded successfully");
        result.put("comparison", progressPercentage);
        result.put("liftName", liftName);

        return result;
    }

    // Helper method to create consistent error results
    private Map<String, Object> createErrorResult(String status, String message, String liftName) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", status);
        result.put("message", message);
        result.put("comparison", 0.0);
        result.put("liftName", liftName);
        return result;
    }

    public Map<String, Object> compareUserMuscleGroup(String tf1s, String tf1e, String tf2s, String tf2e, String muscleGroup, String username){
        Map<String, Object> result = new HashMap<>();
        System.out.println("create result");

        List<String> lifts = completedWorkoutRepository.getLiftsByUserInTwoRangeByMuscleGroup(username, tf1s, tf1e, tf2s, tf2e, muscleGroup);
        System.out.println("after lifts");

        double tf1EstimatedORM;
        double tf2EstimatedORM;
        System.out.println("make estORM");
        List<Double> ormGrowth = new ArrayList<>();
        for(String liftTitle: lifts) {
            tf1EstimatedORM = this.computeAverageTopORM(liftSetRepository.getLiftSetsByUserAndLiftTitleInRange(username, tf1s, tf1e, liftTitle));
            tf2EstimatedORM = this.computeAverageTopORM(liftSetRepository.getLiftSetsByUserAndLiftTitleInRange(username, tf2s, tf2e, liftTitle));
            ormGrowth.add((tf2EstimatedORM-tf1EstimatedORM)/tf1EstimatedORM);
        }
        System.out.println("add ORM TO LIFT");

        double sum = 0;
        if(ormGrowth.isEmpty()) {
            result.put("status", "404");
            result.put("message", "User has no lifts that occur in both timeframes for this muscle group");
            result.put("comparison", 0.0);
            result.put("muscleGroup", muscleGroup);
        }else{
            for(double d: ormGrowth) {
                sum+=d;
            }
            result.put("status", "200");
            result.put("message", "Successfully computed average growth");
            result.put("comparison", sum/ormGrowth.size());
            result.put("muscleGroup", muscleGroup);
        }
        return result;



//        Map<String, Object> result = new HashMap<>();
//        User user = userRepository.findByUsername(username);
//        if(user == null){
//            result.put("status", "404");
//            result.put("message", "User not found");
//            result.put("muscleGroup", muscleGroup);
//
//        } else {
//            List<CompletedWorkout> rangeOneWorkouts = user.getCompletedWorkoutsBetweenDates(LocalDate.parse(tf1s), LocalDate.parse(tf1e));
//            List<CompletedWorkout> rangeTwoWorkouts = user.getCompletedWorkoutsBetweenDates(LocalDate.parse(tf2s), LocalDate.parse(tf2e));
//            Map<String, List<coms309.workout.Set.Set>> t1 = filterLiftsByMuscleGroup(rangeOneWorkouts, muscleGroup);
//            Map<String, List<coms309.workout.Set.Set>> t2 = filterLiftsByMuscleGroup(rangeTwoWorkouts, muscleGroup);
//            List<String> commonLifts = findCommonLifts(t1, t2);
//            if(commonLifts.isEmpty()){
//                result.put("status", "404");
//                result.put("message", "User has no lifts that occur in both timeframes");
//                result.put("comparison", 0.0);
//                result.put("muscleGroup", muscleGroup);
//            }else{
//                double sum = 0.0;
//                int count = 0;
//                double firstORM = 0;
//                double secondORM = 0;
//                for(String lift: commonLifts){
//                    firstORM =computeAverageOverallORM(t1.get(lift));
//                    secondORM =computeAverageOverallORM(t2.get(lift));
//                    sum += (secondORM - firstORM)/firstORM;
//                    System.out.println(lift + " " +firstORM);
//                    System.out.println(lift + " " +secondORM);
//                    count++;
//                }
//                if(count==0){
//                    result.put("status", "404");
//                    result.put("message", "User has no lifts that occur in both timeframes");
//                    result.put("comparison", 0.0);
//                    result.put("muscleGroup", muscleGroup);
//
//                }else{
//                    result.put("status", "200");
//                    result.put("message", "progress recorded successfully");
//                    result.put("comparison", sum/count);
//                    result.put("muscleGroup", muscleGroup);
//                }
//            }
//        }
//        System.out.println("done");
//        return result;
    }
    /**
     * Gets a user's best real one rep max for a particular lift
     */
    public Map<String, Object> getUserMax(String username, String liftTitle){
        Map<String, Object> result = new HashMap<>();
        result.put("liftName", liftTitle);
        result.put("maxWeight", completedWorkoutRepository.getMaxByUserAndContainsKey(username, liftTitle));
        if(result.get("maxWeight")==null){
            result.put("status", "404");
            result.put("message", "User has no max weight");
        }else if(((Double) (result.get("maxWeight")))<=0){
            result.put("status", "404");
            result.put("message", "No lifts found for user");
        }else{
            result.put("status", "200");
            result.put("message", "Lifts found for user");
        }
        return result;
    }

    public CompletedWorkout getLastWorkout(String username){

        User u = userRepository.findByUsername(username);
        List<CompletedWorkout> workouts = u.getCompletedWorkouts();
        int index = 0;
        if (workouts.size()==0 ){
            return null;
        }else {
            for(int i = workouts.size()-1; i>=0; i--){
                if(!(workouts.get(i).getDate().isAfter(LocalDate.now()))){
                    index = i;
                    break;
                }
            }
            return workouts.get(index);
        }
    }

    public Role getUserRole(String username)
    {
        User u = userRepository.findByUsername(username);
        return u.getRole();
    }

    public String getMostPopularLift(String username, String tf1s, String tf1e, String tf2s, String tf2e){
        User u = userRepository.findByUsername(username);
        Map<String, Integer> tf1liftCounter = new HashMap<>();
        Map<String, Integer> tf2liftCounter = new HashMap<>();

        List<CompletedWorkout> rangeOneWorkouts = u.getCompletedWorkoutsBetweenDates(LocalDate.parse(tf1s), LocalDate.parse(tf1e));
        List<CompletedWorkout> rangeTwoWorkouts = u.getCompletedWorkoutsBetweenDates(LocalDate.parse(tf2s), LocalDate.parse(tf2e));

        for(CompletedWorkout w: rangeOneWorkouts){
            for(LiftSet ls: w.getCompletedSets()){
                if(ls.getSets().isEmpty()){
                    continue;
                }else {
                    if (tf1liftCounter.containsKey(ls.getLift().getTitle())) {
                        tf1liftCounter.put(ls.getLift().getTitle(), tf1liftCounter.get(ls.getLift().getTitle()) + 1);
                    } else {
                        tf1liftCounter.put(ls.getLift().getTitle(), 1);
                    }
                }
            }
        }
        for(CompletedWorkout w: rangeTwoWorkouts){
            for(LiftSet ls: w.getCompletedSets()){
                if(ls.getSets().isEmpty()){
                    continue;
                }else {
                    if (tf2liftCounter.containsKey(ls.getLift().getTitle())) {
                        tf2liftCounter.put(ls.getLift().getTitle(), tf2liftCounter.get(ls.getLift().getTitle()) + 1);
                    } else {
                        tf2liftCounter.put(ls.getLift().getTitle(), 1);
                    }
                }
            }
        }
        Set<String> keys1 = tf1liftCounter.keySet();
        Set<String> keys2 = tf2liftCounter.keySet();
        keys1.retainAll(keys2); // Retains only common keys in keys1

        if (keys1.isEmpty()) {
            return null; // No common keys found
        }

        // Find the key with the highest combined value
        String maxKey = null;
        int maxValue = Integer.MIN_VALUE;

        for (String key : keys1) {
            int combinedValue = tf1liftCounter.get(key) + tf2liftCounter.get(key);
            if (combinedValue > maxValue) {
                maxValue = combinedValue;
                maxKey = key;
            }
        }

        return maxKey;
    }
    public String getMostPopularMuscleGroup(String username){
        User u = userRepository.findByUsername(username);
        Map<String, Integer> muscleCounter = new HashMap<>();
        for(CompletedWorkout w: u.getCompletedWorkouts()){
            for(LiftSet ls: w.getCompletedSets()){
                if(muscleCounter.containsKey(ls.getLift().getMuscleGroup())){
                    muscleCounter.put(ls.getLift().getMuscleGroup(), muscleCounter.get(ls.getLift().getMuscleGroup()) + 1);
                }else{
                    muscleCounter.put(ls.getLift().getMuscleGroup(), 1);
                }
            }
        }
        return Collections.max(muscleCounter.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public String getRandomPopularLift(String username,String tf1s, String tf1e, String tf2s, String tf2e){
        List<String> list= completedWorkoutRepository.getLiftsByUserInTwoRange(username, tf1s, tf1e, tf2s, tf2e);
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }
    public String getRandomPopularMuscleGroup(String username,String tf1s, String tf1e, String tf2s, String tf2e){
        List<String> list = completedWorkoutRepository.getMuscleGroupsByUserInTwoRange(username, tf1s, tf1e, tf2s, tf2e);
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        // Return element at random index
        return list.get(randomIndex);
    }
    public String getRandomLift(String username){
        List<String> list = completedWorkoutRepository.getLiftsByUser(username);
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }
}