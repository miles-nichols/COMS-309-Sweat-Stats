package coms309.workout.Report;

import coms309.workout.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>
{
    List<Report> findAll();

    List<Report> findByReported(User reported);

    List<Report> findByCompleted(Boolean completed);

    Report findByReporterAndReportedAndSummary(User reporter, User reported, String summary);
}
