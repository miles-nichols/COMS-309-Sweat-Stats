package coms309.workout.Report;

import coms309.workout.Users.User;
import coms309.workout.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static coms309.workout.Role.Role.ADMIN;

@Service
public class ReportService
{

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Report> getAllIncompleteReports(String adminUsername)
    {
        User admin = userRepository.findByUsername(adminUsername);
        if(admin.getRole() != ADMIN)
        {
            throw new SecurityException("Unauthorized report request");
        }
        return reportRepository.findByCompleted(false);
    }

    public Map<String, String> createReport(String reporter, String reported, String summary, String description)
    {
        Map<String, String> response = new HashMap<>();
        User Reporter = userRepository.findByUsername(reporter);
        User Reported = userRepository.findByUsername(reported);
        Report report = reportRepository.findByReporterAndReportedAndSummary(Reporter, Reported, summary);
        if(report != null) {
            response.put("message", "You've already report this user for this reason");
            response.put("status", "409");
        } else if (Reporter == null || Reported == null) {
            response.put("message", "One of these users does not exist");
            response.put("status", "404");
        } else {
            report = new Report(Reporter, Reported, summary, description);
            reportRepository.save(report);
            response.put("message", "Report created");
            response.put("status", "200");
        }
        return response;
    }

    public List<Report> getReportsByReported(String adminUsername, String username)
    {
        User admin = userRepository.findByUsername(adminUsername);
        if(admin.getRole() != ADMIN)
        {
            throw new SecurityException("Unauthorized report request");
        }
        User reported = userRepository.findByUsername(username);
        return reportRepository.findByReported(reported);
    }

    public Map<String, String> resolveReport(String adminUsername, String reporterUsername, String reportedUsername, String summary)
    {
        Map<String, String> response = new HashMap<>();
        User admin = userRepository.findByUsername(adminUsername);
        User reporter = userRepository.findByUsername(reporterUsername);
        User reported = userRepository.findByUsername(reportedUsername);
        Report report = reportRepository.findByReporterAndReportedAndSummary(reporter, reported, summary);
        if(reporter == null) {
            response.put("message", "Reporter does not exist");
            response.put("status", "404");
        } else if (admin == null) {
            response.put("message", "Admin does not exist");
            response.put("status", "404");
        } else if (admin.getRole() != ADMIN) {
            throw new SecurityException("Unauthorized report request");
        } else if (reported == null) {
            response.put("message", "Reported does not exist");
            response.put("status", "404");
        } else {
            response.put("message", "Report resolved successfully");
            response.put("status", "200");
            int strikes = reported.getStrikes();
            reported.setStrikes(strikes+1);
            userRepository.save(reported);
            report.setCompleted(true);
            reportRepository.save(report);
        }
        return response;
    }

    public Map<String, String> dismissReport(String adminUsername, String reporterUsername, String reportedUsername, String summary)
    {
        Map<String, String> response = new HashMap<>();
        User admin = userRepository.findByUsername(adminUsername);
        User reporter = userRepository.findByUsername(reporterUsername);
        User reported = userRepository.findByUsername(reportedUsername);
        Report report = reportRepository.findByReporterAndReportedAndSummary(reporter, reported, summary);
        if(reporter == null) {
            response.put("message", "Reporter does not exist");
            response.put("status", "404");
        } else if (admin == null) {
            response.put("message", "Admin does not exist");
            response.put("status", "404");
        } else if (admin.getRole() != ADMIN) {
            throw new SecurityException("Unauthorized report request");
        } else if (reported == null) {
            response.put("message", "Reported does not exist");
            response.put("status", "404");
        } else {
            response.put("message", "Report resolved successfully");
            response.put("status", "200");
            report.setCompleted(true);
            reportRepository.save(report);
        }
        return response;
    }
}
