package coms309.workout.Users;

import coms309.workout.Role.Role;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary="Gets all users")
    @GetMapping("/user")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary="Checks if username and password is valid")
    @PostMapping(path = "/user/login")
    public Map<String, String> userLogin(@RequestBody User user) {
        return userService.authenticateUser(user);
    }

    @Operation(summary="Creates new User")
    @PostMapping("/user/signup")
    public @ResponseBody Map<String, String> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @Operation(summary="Changes User's Email")
    @PutMapping("user/signup/email")
    public @ResponseBody Map<String, String> updateUserEmail(@RequestBody User newUser) {
        return userService.updateUserEmail(newUser);
    }

    @Operation(summary="Changes User's Username")
    @PutMapping("user/signup/username")
    public @ResponseBody Map<String, String> updateUserUsername(@RequestBody User newUser) {
        return userService.updateUsername(newUser);
    }

    @Operation(summary="Changes User Password")
    @PutMapping("user/signup/password")
    public @ResponseBody Map<String, String> updateUserPassword(@RequestBody User newUser) {
        return userService.updatePassword(newUser);
    }

    @Operation(summary="Deletes User from Database")
    @DeleteMapping(path = "/user/username/{username}")
    public Map<String, String> deleteUserByUsername(@PathVariable String username) {
        return userService.deleteUser(username);
    }
    @Operation(summary="Used to search for users with first part of their username")
    @GetMapping(path="/user/search/{part_username}")
    public List<User> searchUser(@PathVariable String part_username) {
        return userService.searchUsers(part_username);
    }

    @Operation(summary="follower_username unfollows following_username")
    @PutMapping("/user/unfollow")
    public Map<String, String> unfollow(@RequestParam String follower_username, @RequestParam String following_username) {
        return userService.unfollowUser(follower_username, following_username);
    }

    @Operation(summary="follower_username follows following_username")
    @PutMapping("/user/follow")
    public Map<String, String> follow(@RequestParam String follower_username, @RequestParam String following_username) {
        return userService.followUser(follower_username, following_username);
    }

    @Operation(summary="Gets user by username")
    @GetMapping("/user/{username}")
    public User getUserbyUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @Operation(summary="Gets User's Friends")
    @GetMapping("/user/friend/{username}")
    public Set<User> getFriends(@PathVariable String username) {
        return userService.getUserFriends(username);
    }

    @Operation(summary="Gets User's Followers")
    @GetMapping("/user/followers/{username}")
    public Set<User> getFollowers(@PathVariable String username) {
        return userService.getUserFollowers(username);
    }

    @Operation(summary="Gets Users who User is Following")
    @GetMapping("/user/following/{username}")
    public Set<User> getFollowing(@PathVariable String username) {
        return userService.getUserFollowing(username);
    }

    @Operation(summary="Compares User's lifts from one timeframe to another, if returns 0, then not enough data for calculation")
    @GetMapping("/user/progress/{username}/lift/{liftName}")
    public Map<String, Object> compareLifts(@RequestParam String tf1s, @RequestParam String tf1e, @RequestParam String tf2s, @RequestParam String tf2e, @PathVariable String liftName, @PathVariable String username) {
        return userService.compareUserLifts(tf1s, tf1e, tf2s, tf2e, liftName, username);
    }

    @Operation(summary="Compares User's lifts from one timeframe to another for a certain muscle group and returns the average percentage growth, if returns 0, then not enough data for calculation")
    @GetMapping("/user/progress/{username}/muscleGroup/{muscleGroup}")
    public Map<String, Object> compareMuscles(@RequestParam String tf1s, @RequestParam String tf1e, @RequestParam String tf2s, @RequestParam String tf2e, @PathVariable String muscleGroup, @PathVariable String username) {
        return userService.compareUserMuscleGroup(tf1s, tf1e, tf2s, tf2e, muscleGroup, username);
    }

    @Operation(summary="Returns the role of the user")
    @GetMapping("/user/role")
    public Role returnUserRole(@RequestParam String username)
    {
        return userService.getUserRole(username);
    }

    @GetMapping(path = "/user/maxes/{username}/{liftTitle}")
    public Map<String, Object> getUserLiftMax(@PathVariable String username, @PathVariable String liftTitle) {
        return userService.getUserMax(username, liftTitle);
    }

//    @GetMapping(path = "/user/progress/popular/muscleGroup/{username}")
//    public Map<String, Object> comparePopularMuscles(@RequestParam String tf1s, @RequestParam String tf1e, @RequestParam String tf2s, @RequestParam String tf2e, @PathVariable String username) {
//        return userService.compareUserMuscleGroup(tf1s, tf1e, tf2s, tf2e, userService.getMostPopularMuscleGroup(username), username);
//    }

    @GetMapping(path = "/user/progress/popular/lift/{username}")
    public Map<String, Object> comparePopularLift(@RequestParam String tf1s, @RequestParam String tf1e, @RequestParam String tf2s, @RequestParam String tf2e, @PathVariable String username) {
        return userService.compareUserLifts(tf1s, tf1e, tf2s, tf2e, userService.getMostPopularLift(username, tf1s,  tf1e, tf2s, tf2e), username);
    }

    @GetMapping(path = "/user/progress/random/muscleGroup/{username}")
    public Map<String, Object> compareRandomMuscleGroup(@RequestParam String tf1s, @RequestParam String tf1e, @RequestParam String tf2s, @RequestParam String tf2e, @PathVariable String username) {
        return userService.compareUserMuscleGroup(tf1s, tf1e, tf2s, tf2e, userService.getRandomPopularMuscleGroup(username, tf1s,  tf1e, tf2s, tf2e), username);
    }

    @GetMapping(path = "/user/progress/random/lift/{username}")
    public Map<String, Object> compareRandomLift(@RequestParam String tf1s, @RequestParam String tf1e, @RequestParam String tf2s, @RequestParam String tf2e, @PathVariable String username) {
        return userService.compareUserLifts(tf1s, tf1e, tf2s, tf2e, userService.getRandomPopularLift(username, tf1s,  tf1e, tf2s, tf2e), username);
    }

    @GetMapping(path = "/user/max/random/{username}")
    public Map<String, Object> getRandomMax( @PathVariable String username) {
        return userService.getUserMax(username, userService.getRandomLift(username));
    }
    /**
     * Stuff in Map response
     *
     * status:
     * liftName:
     * maxWeight:
     * message:
     */

}