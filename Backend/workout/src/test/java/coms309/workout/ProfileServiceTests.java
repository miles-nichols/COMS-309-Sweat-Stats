package coms309.workout.Profile;

import coms309.workout.Users.User;
import coms309.workout.Users.UserRepository;
import coms309.workout.CompletedWorkout.CompletedWorkout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileService profileService;

    private User mockUser;
    private Profile mockProfile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockProfile = new Profile("Test User", "Test Bio", "Bench", 225.5, 315.0, 315.0, 185.0);
        mockUser.setProfile(mockProfile);
        mockUser.setUsername("testuser");
    }


    @Test
    void testGetAndSetId() {
        Profile profile = new Profile();
        profile.setId(1L);
        assertEquals(1L, profile.getId());
    }

    @Test
    void testGetAndSetName() {
        Profile profile = new Profile();
        profile.setName("John Doe");
        assertEquals("John Doe", profile.getName());
    }

    @Test
    void testGetAndSetBio() {
        Profile profile = new Profile();
        profile.setBio("Fitness enthusiast");
        assertEquals("Fitness enthusiast", profile.getBio());
    }

    @Test
    void testGetAndSetFavLiftName() {
        Profile profile = new Profile();
        profile.setFavLiftName("Bench Press");
        assertEquals("Bench Press", profile.getFavLiftName());
    }

    @Test
    void testGetAndSetMaxDead() {
        Profile profile = new Profile();
        profile.setMax_dead(300.0);
        assertEquals(300.0, profile.getMax_dead());
    }

    @Test
    void testGetAndSetMaxSquat() {
        Profile profile = new Profile();
        profile.setMax_squat(250.0);
        assertEquals(250.0, profile.getMax_squat());
    }

    @Test
    void testGetAndSetMaxClean() {
        Profile profile = new Profile();
        profile.setMax_clean(180.0);
        assertEquals(180.0, profile.getMax_clean());
    }

    @Test
    void testGetAndSetMaxBench() {
        Profile profile = new Profile();
        profile.setMax_bench(200.0);
        assertEquals(200.0, profile.getMax_bench());
    }

    @Test
    void testGetAndSetUser() {
        Profile profile = new Profile();
        User user = new User();
        profile.setUser(user);
        assertEquals(user, profile.getUser());
    }

    @Test
    void testGetAndSetFilePath() {
        Profile profile = new Profile();
        profile.setFilePath("/custom/path/to/profile.jpg");
        assertEquals("/custom/path/to/profile.jpg", profile.getFilePath());
    }

    @Test
    void testCopyProfile() {
        Profile profile1 = new Profile("John", "Loves lifting", "Squat", 150.0, 200.0, 180.0, 100.0);
        Profile profile2 = new Profile();
        profile2.copyProfile(profile1);

        assertEquals("John", profile2.getName());
        assertEquals("Loves lifting", profile2.getBio());
        assertEquals("Squat", profile2.getFavLiftName());
        assertEquals(150.0, profile2.getMax_bench());
        assertEquals(200.0, profile2.getMax_dead());
        assertEquals(180.0, profile2.getMax_squat());
        assertEquals(100.0, profile2.getMax_clean());
    }

    @Test
    void testDefaultFilePath() {
        Profile profile = new Profile();
        assertEquals("/target/profile_pictures/default_profile_pic.jpg", profile.getFilePath());
    }


    @Test
    void testGetProfileByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);

        Profile retrievedProfile = profileService.getProfileByUsername("testuser");

        assertEquals(mockProfile, retrievedProfile);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testCreateProfile() {
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);

        Profile createdProfile = profileService.createProfile("testuser");

        assertNotNull(createdProfile);
        verify(userRepository).save(mockUser);
    }

    @Test
    void testUpdateProfile() {
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);

        Profile updateProfile = new Profile("Updated Name", "Updated Bio", "Deadlift", 235.5, 335.0, 325.0, 195.0);
        Profile updatedProfile = profileService.updateProfile("testuser", updateProfile);

        assertEquals("Updated Name", updatedProfile.getName());
        assertEquals("Updated Bio", updatedProfile.getBio());
        verify(userRepository).save(mockUser);
    }


    @Test
    void testGetUserLiftMax() {
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);

        Map<String, Object> benchMax = profileService.getUserLiftMax("testuser", "bench");
        Map<String, Object> deadliftMax = profileService.getUserLiftMax("testuser", "deadlift");
        Map<String, Object> squatMax = profileService.getUserLiftMax("testuser", "squat");
        Map<String, Object> cleanMax = profileService.getUserLiftMax("testuser", "clean");

        assertEquals(225.5, benchMax.get("max"));
        assertEquals(315.0, deadliftMax.get("max"));
        assertEquals(315.0, squatMax.get("max"));
        assertEquals(185.0, cleanMax.get("max"));
    }

    @Test
    void testGetUserLiftMax_InvalidLift() {
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);

        assertThrows(IllegalArgumentException.class,
                () -> profileService.getUserLiftMax("testuser", "invalid_lift")
        );
    }

    @Test
    void testGetProfileByUsername_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> profileService.getProfileByUsername("nonexistent")
        );
    }
}
