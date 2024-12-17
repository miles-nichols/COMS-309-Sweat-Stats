package coms309.workout.Report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import coms309.workout.CompletedWorkout.CompletedWorkout;
import coms309.workout.Profile.Profile;
import coms309.workout.Role.Role;
import coms309.workout.Users.User;
import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "reports")
public class Report
{
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "reporter")
    @JsonIgnoreProperties({"password", "email", "friends", "following", "followers", "profile", "completedWorkouts"}) // This ensures reported user's password and other fields are ignored
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "reported")
    @JsonIgnoreProperties({"password", "email", "friends", "following", "followers", "profile", "completedWorkouts"}) // This ensures reported user's password and other fields are ignored
    private User reported;

    @Column
    private String summary;

    @Column
    private String description;

    @Column
    private LocalDateTime reportDate;

    @Column
    private boolean completed;

    public Report() {}

    public Report(User reporter, User reported, String summary, String description)
    {
        this.reporter = reporter;
        this.reported = reported;
        this.summary = summary;
        this.description = description;
        reportDate = LocalDateTime.now();
        this.completed = false;
    }

    public int getReportId() { return id; }

    public User getReported() { return reported; }

    public boolean getCompleted() { return completed; }

    public void setCompleted(boolean temp) { this.completed = temp; }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }


    @JsonProperty("reported_username") // Rename in JSON response
    public String getReportedUsername() {
        return reported.getUsername();
    }

    @JsonProperty("reporter_username") // Rename in JSON response
    public String getReporterUsername() {
        return reporter.getUsername();
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User newReporter) {
        reporter = newReporter;
    }

    public void setReported(User newReported) {
        reported = newReported;
    }

    public void setReportDate(LocalDateTime newDate) {
        reportDate = newDate;
    }

    public void setSummary(String updatedSummary) {
        summary = updatedSummary;
    }

    public void setDescription(String updatedDescription) {
        description = updatedDescription;
    }
}
