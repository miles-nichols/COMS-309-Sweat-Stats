package coms309.workout.Lifts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LiftTest {

    @Test
    void testGetAndSetTitle() {
        Lift lift = new Lift();
        lift.setTitle("Bench Press");
        assertEquals("Bench Press", lift.getTitle());
    }

    @Test
    void testGetAndSetDescription() {
        Lift lift = new Lift();
        lift.setDescription("A strength training exercise");
        assertEquals("A strength training exercise", lift.getDescription());
    }

    @Test
    void testGetAndSetType() {
        Lift lift = new Lift();
        lift.setType("Strength");
        assertEquals("Strength", lift.getType());
    }

    @Test
    void testGetAndSetMuscleGroup() {
        Lift lift = new Lift();
        lift.setMuscleGroup("Chest");
        assertEquals("Chest", lift.getMuscleGroup());
    }

    @Test
    void testGetAndSetEquipment() {
        Lift lift = new Lift();
        lift.setEquipment("Barbell");
        assertEquals("Barbell", lift.getEquipment());
    }

    @Test
    void testGetAndSetLevel() {
        Lift lift = new Lift();
        lift.setLevel("Intermediate");
        assertEquals("Intermediate", lift.getLevel());
    }

    @Test
    void testCopy() {
        Lift lift1 = new Lift("Bench Press", "A strength training exercise", "Strength", "Chest", "Barbell", "Intermediate");
        Lift lift2 = new Lift();
        lift2.copy(lift1);

        assertEquals("Bench Press", lift2.getTitle());
        assertEquals("A strength training exercise", lift2.getDescription());
        assertEquals("Strength", lift2.getType());
        assertEquals("Chest", lift2.getMuscleGroup());
        assertEquals("Barbell", lift2.getEquipment());
        assertEquals("Intermediate", lift2.getLevel());
    }

    @Test
    void testConstructor() {
        Lift lift = new Lift("Deadlift", "A full-body exercise", "Strength", "Back", "Barbell", "Advanced");

        assertEquals("Deadlift", lift.getTitle());
        assertEquals("A full-body exercise", lift.getDescription());
        assertEquals("Strength", lift.getType());
        assertEquals("Back", lift.getMuscleGroup());
        assertEquals("Barbell", lift.getEquipment());
        assertEquals("Advanced", lift.getLevel());
    }
}