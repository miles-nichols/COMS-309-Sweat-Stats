package coms309.workout.Report;

import coms309.workout.Users.User;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReportController
{
    @Autowired
    private ReportService reportService;

    @Operation(summary = "Gets all non-completed reports if user is of ADMIN status")
    @GetMapping("/report")
    public List<Report> getAllIncompleteReports()
    {
        return reportService.getAllIncompleteReports("a");
    }

    @Operation(summary = "Gets reports based on the reported user")
    @GetMapping("/report/{username}/")
    public List<Report> getReportsByReported(@PathVariable String username)
    {
        return reportService.getReportsByReported("a", username);
    }

    @Operation(summary = "Posts a new report object")
    @PostMapping("/report/{reporter}/{reported}")
    public @ResponseBody Map<String, String> createReport(@PathVariable String reporter, @PathVariable String reported, @RequestParam String summary, @RequestParam String description)
    {
        return reportService.createReport(reporter, reported, summary, description);
    }

    @Operation(summary = "Resolve a report and add a strike to reported User")
    @PutMapping("/report/resolve")
    public Map<String, String> resolveReport(@RequestParam String reporterUsername, @RequestParam String reportedUsername, @RequestParam String summary)
    {
        return reportService.resolveReport("a", reporterUsername, reportedUsername, summary);
    }

    @Operation(summary = "Resolve a report and add a strike to reported User")
    @PutMapping("/report/dismiss")
    public Map<String, String> dismissReport(@RequestParam String reporterUsername, @RequestParam String reportedUsername, @RequestParam String summary)
    {
        return reportService.dismissReport("a", reporterUsername, reportedUsername, summary);
    }

}
