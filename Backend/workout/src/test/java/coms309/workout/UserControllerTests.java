package coms309.workout.Users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> mockUsers = Arrays.asList(
                new User("user1", "pass1", "email1@test.com"),
                new User("user2", "pass2", "email2@test.com")
        );
        when(userService.getAllUsers()).thenReturn(mockUsers);

        // Act
        List<User> result = userController.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testUserLogin_Successful() {
        // Arrange
        User loginUser = new User("testuser", "password", "test@email.com");
        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("message", "Login successful");
        expectedResult.put("status", "200");

        when(userService.authenticateUser(loginUser)).thenReturn(expectedResult);

        // Act
        Map<String, String> result = userController.userLogin(loginUser);

        // Assert
        assertEquals("Login successful", result.get("message"));
        assertEquals("200", result.get("status"));
        verify(userService, times(1)).authenticateUser(loginUser);
    }

    @Test
    void testCreateUser_Successful() {
        // Arrange
        User newUser = new User("newuser", "password", "new@email.com");
        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("message", "New User posted correctly");
        expectedResult.put("status", "200");

        when(userService.createUser(newUser)).thenReturn(expectedResult);

        // Act
        Map<String, String> result = userController.createUser(newUser);

        // Assert
        assertEquals("New User posted correctly", result.get("message"));
        assertEquals("200", result.get("status"));
        verify(userService, times(1)).createUser(newUser);
    }

    @Test
    void testUpdateUserEmail() {
        // Arrange
        User userWithNewEmail = new User("testuser", "password", "new@email.com");
        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("message", "Student email was updated successfully: new@email.com");
        expectedResult.put("status", "200");

        when(userService.updateUserEmail(userWithNewEmail)).thenReturn(expectedResult);

        // Act
        Map<String, String> result = userController.updateUserEmail(userWithNewEmail);

        // Assert
        assertEquals("Student email was updated successfully: new@email.com", result.get("message"));
        assertEquals("200", result.get("status"));
        verify(userService, times(1)).updateUserEmail(userWithNewEmail);
    }

    @Test
    void testDeleteUserByUsername() {
        // Arrange
        String username = "testuser";
        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("message", "User deleted successfully");
        expectedResult.put("status", "200");

        when(userService.deleteUser(username)).thenReturn(expectedResult);

        // Act
        Map<String, String> result = userController.deleteUserByUsername(username);
        // Assert
        assertEquals("User deleted successfully", result.get("message"));
        assertEquals("200", result.get("status"));
        verify(userService, times(1)).deleteUser(username);
    }

    @Test
    void testSearchUser() {
        // Arrange
        String partUsername = "test";
        List<User> expectedUsers = Arrays.asList(
                new User("testuser1", "pass1", "test1@email.com"),
                new User("testuser2", "pass2", "test2@email.com")
        );

        when(userService.searchUsers(partUsername)).thenReturn(expectedUsers);
        // Act
        List<User> result = userController.searchUser(partUsername);

        // Assert
        assertEquals(2, result.size());
        verify(userService, times(1)).searchUsers(partUsername);
    }

    @Test
    void testFollowUser() {
        // Arrange
        String followerUsername = "follower";
        String followingUsername = "following";
        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("message", "Follow successful");
        expectedResult.put("status", "200");

        when(userService.followUser(followerUsername, followingUsername)).thenReturn(expectedResult);

        // Act
        Map<String, String> result = userController.follow(followerUsername, followingUsername);

        // Assert
        assertEquals("Follow successful", result.get("message"));
        assertEquals("200", result.get("status"));
        verify(userService, times(1)).followUser(followerUsername, followingUsername);
    }

    @Test
    void testUnfollowUser() {
        // Arrange
        String followerUsername = "follower";
        String followingUsername = "following";
        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("message", "Successfully unfollowed");
        expectedResult.put("status", "200");

        when(userService.unfollowUser(followerUsername, followingUsername)).thenReturn(expectedResult);

        // Act
        Map<String, String> result = userController.unfollow(followerUsername, followingUsername);

        // Assert
        assertEquals("Successfully unfollowed", result.get("message"));
        assertEquals("200", result.get("status"));
        verify(userService, times(1)).unfollowUser(followerUsername, followingUsername);
    }

    @Test
    void testGetUserByUsername() {
        // Arrange
        String username = "testuser";
        User expectedUser = new User(username, "password", "test@email.com");

        when(userService.getUserByUsername(username)).thenReturn(expectedUser);

        // Act
        User result = userController.getUserbyUsername(username);

        // Assert
        assertEquals(username, result.getUsername());
        verify(userService, times(1)).getUserByUsername(username);
    }

    @Test
    void testCompareLifts() {
        // Arrange
        String username = "testuser";
        String liftName = "Squat";
        String tf1s = "2023-01-01";
        String tf1e = "2023-03-31";
        String tf2s = "2023-04-01";
        String tf2e = "2023-06-30";

        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("status", "200");
        expectedResult.put("message", "progress recorded successfully");
        expectedResult.put("comparison", 0.15);

        when(userService.compareUserLifts(tf1s, tf1e, tf2s, tf2e, liftName, username))
                .thenReturn(expectedResult);

        // Act
        Map<String, Object> result = userController.compareLifts(tf1s, tf1e, tf2s, tf2e, liftName, username);

        // Assert
        assertEquals("200", result.get("status"));
        assertEquals("progress recorded successfully", result.get("message"));
        assertEquals(0.15, result.get("comparison"));
        verify(userService, times(1)).compareUserLifts(tf1s, tf1e, tf2s, tf2e, liftName, username);
    }

    @Test
    void testCompareMuscles() {
        // Arrange
        String username = "testuser";
        String muscleGroup = "Legs";
        String tf1s = "2023-01-01";
        String tf1e = "2023-03-31";
        String tf2s = "2023-04-01";
        String tf2e = "2023-06-30";

        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("status", "200");
        expectedResult.put("message", "progress recorded successfully");
        expectedResult.put("comparison", 0.2);

        when(userService.compareUserMuscleGroup(tf1s, tf1e, tf2s, tf2e, muscleGroup, username))
                .thenReturn(expectedResult);

        // Act
        Map<String, Object> result = userController.compareMuscles(tf1s, tf1e, tf2s, tf2e, muscleGroup, username);

        // Assert
        assertEquals("200", result.get("status"));
        assertEquals("progress recorded successfully", result.get("message"));
        assertEquals(0.2, result.get("comparison"));
        verify(userService, times(1)).compareUserMuscleGroup(tf1s, tf1e, tf2s, tf2e, muscleGroup, username);
    }
}