package coms309.workout.Profile;

import coms309.workout.Users.User;
import coms309.workout.Users.UserRepository;
import coms309.workout.Users.UserService;
import coms309.workout.CompletedWorkout.CompletedWorkout;
import coms309.workout.Chat.Message;
import coms309.workout.Chat.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProfileControllerTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository msgRepo;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProfileController profileController;

    private User mockUser;
    private Profile mockProfile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setUsername("testuser");

        mockProfile = new Profile();
        mockProfile.setFilePath("/target/profile_pictures/testuser_profile_pic");

        mockUser.setProfile(mockProfile);
    }

    @Test
    public void testGetProfile() {

        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);

        // Act
        Profile retrievedProfile = profileController.getProfile("testuser");

        // Assert
        assertNotNull(retrievedProfile);
    }

    @Test
    public void testMakeProfile() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);

        // Act
        profileController.makeProfile("testuser");

        // Assert
        assertNotNull(mockUser.getProfile());
        verify(userRepository).save(mockUser);
    }

    @Test
    public void testUpdateProfile() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);

        Profile updatedProfile = new Profile();

        // Act
        Map<String, String> response = profileController.updateProfile("testuser", updatedProfile);

        // Assert
        assertEquals("Profile set successfully", response.get("message"));
        assertEquals("200", response.get("status"));
        verify(userRepository).save(mockUser);
    }


    @Test
    public void testGetLastWorkout() {
        // Arrange
        CompletedWorkout mockWorkout = new CompletedWorkout();
        when(userService.getLastWorkout("testuser")).thenReturn(mockWorkout);

        // Act
        CompletedWorkout retrievedWorkout = profileController.getLastWorkout("testuser");

        // Assert
        assertEquals(mockWorkout, retrievedWorkout);
    }
//
//    @Test
//    public void testGetProfilePicture() throws IOException {
//        // Arrange
//        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
//
//        // Act
//        byte[] profilePicture = profileController.getProfilePictureByUsername("testuser");
//
//        // Assert
//        assertNotNull(profilePicture);
//    }

//    @Test
//    public void testHandleFileUpload() throws IOException {
//        // Arrange
//        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
//        MultipartFile mockFile = new MockMultipartFile(
//                "image",
//                "testpic.jpg",
//                "image/jpeg",
//                "test image content".getBytes()
//        );
//
//        // Act
//        String result = profileController.handleFileUpload(mockFile, "testuser");
//
//        // Assert
//        assertTrue(result.contains("File uploaded successfully"));
//        verify(userRepository).save(mockUser);
//    }

    @Test
    public void testGetMessages() {
        // Arrange
        Message msg1 = new Message();
        msg1.setSender("testuser");
        msg1.setContent("Hello");

        Message msg2 = new Message();
        msg2.setSender("otheruser");
        msg2.setContent("Hi there");

        List<Message> mockMessages = Arrays.asList(msg1, msg2);
        when(msgRepo.findBySenderOrRecipientStartingWithOrderBySentAsc("testuser", "test"))
                .thenReturn(mockMessages);

        // Act
        String messageString = profileController.getMessages("testuser", "test");

        // Assert
        assertTrue(messageString.contains("testuser: Hello"));
        assertTrue(messageString.contains("otheruser: Hi there"));
    }

}
