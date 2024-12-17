package coms309.workout.Lifts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LiftControllerTests{

    @Mock
    private LiftRepository liftRepository;

    @InjectMocks
    private LiftController liftController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetLift_WhenLiftExists() {
        // Arrange
        int liftId = 1;
        Lift mockLift = new Lift("Bench Press", "Chest exercise", "Strength", "Chest", "Barbell", "Intermediate");
        when(liftRepository.findById(liftId)).thenReturn(mockLift);

        // Act
        Map<String, Object> response = liftController.getLift(liftId);

        // Assert
        assertEquals("200", response.get("status"));
        assertEquals(mockLift, response.get("lift"));
    }

    @Test
    public void testGetLift_WhenLiftNotExists() {
        // Arrange
        int liftId = 999;
        when(liftRepository.findById(liftId)).thenReturn(null);

        // Act
        Map<String, Object> response = liftController.getLift(liftId);

        // Assert
        assertEquals("404", response.get("status"));
        assertEquals("Lift not found with id: 999", response.get("message"));
    }

    @Test
    public void testGetAllLifts() {
        // Arrange
        List<Lift> mockLifts = new ArrayList<>();
        mockLifts.add(new Lift("Squat", "Leg exercise", "Strength", "Legs", "Barbell", "Advanced"));
        mockLifts.add(new Lift("Deadlift", "Back exercise", "Strength", "Back", "Barbell", "Advanced"));
        when(liftRepository.findAll()).thenReturn(mockLifts);

        // Act
        List<Lift> lifts = liftController.getAllLifts();

        // Assert
        assertEquals(2, lifts.size());
        verify(liftRepository).findAll();
    }

    @Test
    public void testGetLiftByTitle() {
        // Arrange
        String title = "Bench Press";
        Lift mockLift = new Lift(title, "Chest exercise", "Strength", "Chest", "Barbell", "Intermediate");
        when(liftRepository.findByTitle(title)).thenReturn(mockLift);

        // Act
        List<Lift> lifts = liftController.getLiftByTitle(title);

        // Assert
        assertEquals(1, lifts.size());
        assertEquals(title, lifts.get(0).getTitle());
    }

    @Test
    public void testCreateNewLift_WhenLiftDoesNotExist() {
        // Arrange
        Lift newLift = new Lift("New Lift", "Description", "Type", "Muscle Group", "Equipment", "Level");
        when(liftRepository.findByTitle(newLift.getTitle())).thenReturn(null);

        // Act
        Map<String, String> response = liftController.createNewLift(newLift);

        // Assert
        assertEquals("200", response.get("status"));
        assertEquals("New lift posted correctly", response.get("message"));
        verify(liftRepository).save(newLift);
    }

    @Test
    public void testCreateNewLift_WhenLiftAlreadyExists() {
        // Arrange
        Lift existingLift = new Lift("Existing Lift", "Description", "Type", "Muscle Group", "Equipment", "Level");
        when(liftRepository.findByTitle(existingLift.getTitle())).thenReturn(existingLift);

        // Act
        Map<String, String> response = liftController.createNewLift(existingLift);

        // Assert
        assertEquals("409", response.get("status"));
        assertEquals("Lift already exists", response.get("message"));
        verify(liftRepository, never()).save(existingLift);
    }

    @Test
    public void testUpdateLift_WhenLiftExists() {
        // Arrange
        int liftId = 1;
        Lift oldLift = new Lift("Old Lift", "Old Description", "Old Type", "Old Muscle Group", "Old Equipment", "Beginner");
        Lift newLift = new Lift("New Lift", "New Description", "New Type", "New Muscle Group", "New Equipment", "Advanced");

        when(liftRepository.findById(liftId)).thenReturn(oldLift);

        // Act
        Map<String, String> response = liftController.updateLift(liftId, newLift);

        // Assert
        assertEquals("200", response.get("status"));
        assertEquals("Lift was updated successfully", response.get("message"));
        assertEquals(newLift.getTitle(), oldLift.getTitle());
        assertEquals(newLift.getDescription(), oldLift.getDescription());
        verify(liftRepository).save(oldLift);
    }

    @Test
    public void testUpdateLift_WhenLiftNotExists() {
        // Arrange
        int liftId = 999;
        Lift newLift = new Lift("New Lift", "New Description", "New Type", "New Muscle Group", "New Equipment", "Advanced");

        when(liftRepository.findById(liftId)).thenReturn(null);

        // Act
        Map<String, String> response = liftController.updateLift(liftId, newLift);

        // Assert
        assertEquals("404", response.get("status"));
        assertEquals("Lift not found with id: 999", response.get("message"));
        verify(liftRepository, never()).save(any());
    }

    @Test
    public void testDeleteLift_WhenLiftExists() {
        // Arrange
        int liftId = 1;
        Lift existingLift = new Lift("Lift to Delete", "Description", "Type", "Muscle Group", "Equipment", "Level");

        when(liftRepository.findById(liftId)).thenReturn(existingLift);

        // Act
        Map<String, String> response = liftController.deleteLift(liftId);

        // Assert
        assertEquals("200", response.get("status"));
        assertEquals("User was Deleted successfully", response.get("message"));
        verify(liftRepository).deleteById(liftId);
    }

    @Test
    public void testDeleteLift_WhenLiftNotExists() {
        // Arrange
        int liftId = 999;

        when(liftRepository.findById(liftId)).thenReturn(null);

        // Act
        Map<String, String> response = liftController.deleteLift(liftId);

        // Assert
        assertEquals("404", response.get("status"));
        assertEquals("Lift not found with id: 999", response.get("message"));
        verify(liftRepository, never()).deleteById(anyInt());
    }

    @Test
    public void testGetLiftByEquipment() {
        // Arrange
        String equipment = "Barbell";
        List<Lift> mockLifts = new ArrayList<>();
        mockLifts.add(new Lift("Squat", "Leg exercise", "Strength", "Legs", equipment, "Advanced"));
        mockLifts.add(new Lift("Bench Press", "Chest exercise", "Strength", "Chest", equipment, "Intermediate"));

        when(liftRepository.findByEquipment(equipment)).thenReturn(mockLifts);

        // Act
        List<Lift> lifts = liftController.getLiftByEquipment(equipment);

        // Assert
        assertEquals(2, lifts.size());
        assertTrue(lifts.stream().allMatch(lift -> lift.getEquipment().equals(equipment)));
    }

    @Test
    public void testGetLiftByMuscleGroup() {
        // Arrange
        String muscleGroup = "Chest";
        List<Lift> mockLifts = new ArrayList<>();
        mockLifts.add(new Lift("Bench Press", "Chest exercise", "Strength", muscleGroup, "Barbell", "Intermediate"));
        mockLifts.add(new Lift("Incline Press", "Upper chest exercise", "Strength", muscleGroup, "Dumbbell", "Advanced"));

        when(liftRepository.findByMuscleGroup(muscleGroup)).thenReturn(mockLifts);

        // Act
        List<Lift> lifts = liftController.getLiftByMuscleGroup(muscleGroup);

        // Assert
        assertEquals(2, lifts.size());
        assertTrue(lifts.stream().allMatch(lift -> lift.getMuscleGroup().equals(muscleGroup)));
    }

    @Test
    public void testGetLiftByType() {
        // Arrange
        String type = "Strength";
        List<Lift> mockLifts = new ArrayList<>();
        mockLifts.add(new Lift("Squat", "Leg exercise", type, "Legs", "Barbell", "Advanced"));
        mockLifts.add(new Lift("Bench Press", "Chest exercise", type, "Chest", "Barbell", "Intermediate"));

        when(liftRepository.findByType(type)).thenReturn(mockLifts);

        // Act
        List<Lift> lifts = liftController.getLiftByType(type);

        // Assert
        assertEquals(2, lifts.size());
        assertTrue(lifts.stream().allMatch(lift -> lift.getType().equals(type)));
    }

    @Test
    public void testFindPossibleLifts() {
        // Arrange
        String searchTerm = "press";
        List<Lift> mockLifts = new ArrayList<>();
        mockLifts.add(new Lift("Bench Press", "Chest exercise", "Strength", "Chest", "Barbell", "Intermediate"));
        mockLifts.add(new Lift("Overhead Press", "Shoulder exercise", "Strength", "Shoulders", "Barbell", "Advanced"));

        when(liftRepository.findByTitleContains(searchTerm)).thenReturn(mockLifts);

        // Act
        List<Lift> lifts = liftController.findPossibleLifts(searchTerm);

        // Assert
        assertEquals(2, lifts.size());
        assertTrue(lifts.stream().allMatch(lift -> lift.getTitle().toLowerCase().contains(searchTerm.toLowerCase())));
    }
}