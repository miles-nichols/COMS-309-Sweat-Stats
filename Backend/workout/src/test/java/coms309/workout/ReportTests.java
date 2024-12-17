package coms309.workout.Report;

import coms309.workout.Users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReportTests {

    private Report report;
    private User reporter;
    private User reported;

    @BeforeEach
    void setUp() {
        reporter = new User("reporter", "password", "reporter@example.com");
        reported = new User("reported", "password", "reported@example.com");
        report = new Report(reporter, reported, "Summary of the report", "Detailed description of the report");
    }

    @Test
    void testReportInitialization() {
        // Assert
        assertNotNull(report);
        assertEquals("Summary of the report", report.getSummary());
        assertEquals("Detailed description of the report", report.getDescription());
        assertEquals(reporter, report.getReporter());
        assertEquals(reported, report.getReported());
        assertFalse(report.getCompleted());
        assertNotNull(report.getReportDate());
    }

    @Test
    void testSetCompleted() {
        // Act
        report.setCompleted(true);

        // Assert
        assertTrue(report.getCompleted());
    }

    @Test
    void testSetSummary() {
        // Act
        report.setSummary("Updated Summary");

        // Assert
        assertEquals("Updated Summary", report.getSummary());
    }

    @Test
    void testSetDescription() {
        // Act
        report.setDescription("Updated Description");

        // Assert
        assertEquals("Updated Description", report.getDescription());
    }

    @Test
    void testSetReportDate() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.of(2023, 12, 31, 23, 59);

        // Act
        report.setReportDate(newDate);

        // Assert
        assertEquals(newDate, report.getReportDate());
    }

    @Test
    void testSetReported() {
        // Arrange
        User newReported = new User("newReported", "password", "newReported@example.com");

        // Act
        report.setReported(newReported);

        // Assert
        assertEquals(newReported, report.getReported());
    }

    @Test
    void testSetReporter() {
        // Arrange
        User newReporter = new User("newReporter", "password", "newReporter@example.com");

        // Act
        report.setReporter(newReporter);

        // Assert
        assertEquals(newReporter, report.getReporter());
    }

    @Test
    void testGetReportedUsername() {
        // Assert
        assertEquals("reported", report.getReportedUsername());
    }

    @Test
    void testGetReporterUsername() {
        // Assert
        assertEquals("reporter", report.getReporterUsername());
    }

    @Test
    void testReportDateIsRecent() {
        // Assert
        assertTrue(report.getReportDate().isBefore(LocalDateTime.now()));
        assertTrue(report.getReportDate().isAfter(LocalDateTime.now().minusMinutes(10)));
    }
}
