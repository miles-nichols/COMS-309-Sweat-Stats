package coms309.workout.ActivityFeed;

import coms309.workout.CompletedWorkout.CompletedWorkout;
import jakarta.persistence.PostPersist;
import org.springframework.stereotype.Component;

@Component
public class CompletedWorkoutListener {

    private final ActivityFeedHandler activityFeedHandler;

    public CompletedWorkoutListener(ActivityFeedHandler activityFeedHandler) {
        this.activityFeedHandler = activityFeedHandler;
    }

    @PostPersist
    public void onCompletedWorkoutPersist(CompletedWorkout completedWorkout) {
        String message = completedWorkout.getUser().getUsername() + " completed: " + completedWorkout.getName();
        activityFeedHandler.broadcast(message);
    }
}
