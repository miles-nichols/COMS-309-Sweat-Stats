package coms309.workout;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import coms309.workout.Users.User;
import coms309.workout.Users.UserRepository;
import coms309.workout.Users.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MattUserControllerTests {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUserByUsernameTest() {
        when(userRepository.findByUsername("jDoe"))
                .thenReturn(new User("jDoe", "password123", "jDoe@gmail.com"));

        User user = userService.getUserByUsername("jDoe");

        assertEquals("jDoe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("jDoe@gmail.com", user.getEmail());
    }

    @Test
    public void getAllUsersTest() {
        List<User> list = new ArrayList<User>();
        User userOne = new User("John", "pass1", "john@gmail.com");
        User userTwo = new User("Alex", "pass2", "alex@yahoo.com");
        User userThree = new User("Steve", "pass3", "steve@gmail.com");

        list.add(userOne);
        list.add(userTwo);
        list.add(userThree);

        when(userRepository.findAll()).thenReturn(list);

        List<User> userList = userService.getAllUsers();

        assertEquals(3, userList.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void authenticateUserSuccessTest() {
        User testUser = new User("testUser", "password123", "test@email.com");
        when(userRepository.findByUsername("testUser")).thenReturn(testUser);

        Map<String, String> result = userService.authenticateUser(testUser);

        assertEquals("200", result.get("status"));
        assertEquals("Login successful", result.get("message"));
    }

    @Test
    public void authenticateUserFailTest() {
        User storedUser = new User("testUser", "correctPassword", "test@email.com");
        User loginUser = new User("testUser", "wrongPassword", "test@email.com");

        when(userRepository.findByUsername("testUser")).thenReturn(storedUser);

        Map<String, String> result = userService.authenticateUser(loginUser);

        assertEquals("401", result.get("status"));
        assertEquals("Login failed", result.get("message"));
    }

    @Test
    public void createUserTest() {
        User newUser = new User("newUser", "password123", "new@email.com");
        when(userRepository.findByUsername("newUser")).thenReturn(null);
        when(userRepository.findByEmail("new@email.com")).thenReturn(null);

        Map<String, String> result = userService.createUser(newUser);

        assertEquals("200", result.get("status"));
        assertEquals("New User posted correctly", result.get("message"));
    }

    @Test
    public void createUserExistingUsernameTest() {
        User existingUser = new User("existingUser", "password123", "existing@email.com");
        when(userRepository.findByUsername("existingUser")).thenReturn(existingUser);

        Map<String, String> result = userService.createUser(existingUser);

        assertEquals("409", result.get("status"));
        assertEquals("Username already exists", result.get("message"));
    }

    @Test
    public void updateUserEmailTest() {
        User existingUser = new User("user1", "password123", "old@email.com");
        User updatedUser = new User("user1", "password123", "new@email.com");

        when(userRepository.findByUsername("user1")).thenReturn(existingUser);

        Map<String, String> result = userService.updateUserEmail(updatedUser);

        assertEquals("200", result.get("status"));
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void deleteUserTest() {
        User existingUser = new User("userToDelete", "password123", "delete@email.com");
        when(userRepository.findByUsername("userToDelete")).thenReturn(existingUser);

        Map<String, String> result = userService.deleteUser("userToDelete");

        assertEquals("200", result.get("status"));
        assertEquals("User deleted successfully", result.get("message"));
        verify(userRepository, times(1)).deleteByUsername("userToDelete");
    }

    @Test
    public void followUserTest() {
        User follower = new User("follower", "pass1", "follower@email.com");
        User following = new User("following", "pass2", "following@email.com");

        when(userRepository.findByUsername("follower")).thenReturn(follower);
        when(userRepository.findByUsername("following")).thenReturn(following);

        Map<String, String> result = userService.followUser("follower", "following");

        assertEquals("200", result.get("status"));
        verify(userRepository, times(1)).save(following);
        verify(userRepository, times(1)).save(follower);

    }

    @Test
    public void unfollowUserTest() {
        User follower = new User("follower", "pass1", "follower@email.com");
        User following = new User("following", "pass2", "following@email.com");

        following.getFollowers().add(follower);
        follower.getFollowing().add(following);

        when(userRepository.findByUsername("follower")).thenReturn(follower);
        when(userRepository.findByUsername("following")).thenReturn(following);

        Map<String, String> result = userService.unfollowUser("follower", "following");

        assertEquals("200", result.get("status"));
        assertEquals("Successfully unfollowed", result.get("message"));
        verify(userRepository, times(1)).save(following);
        verify(userRepository, times(1)).save(follower);

    }

    @Test
    public void getUserFriendsTest() {
        User user = new User("testUser", "pass1", "test@email.com");
        User friend = new User("friend", "pass2", "friend@email.com");
        user.getFriends().add(friend);

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        Set<User> friends = userService.getUserFriends("testUser");

        assertEquals(1, friends.size());
        assertEquals("friend", friends.iterator().next().getUsername());
    }
}