package coms309.workout.ActivityFeed;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ActivityFeedSocket {

    // Example to demonstrate broadcasting (optional)
    @MessageMapping("/sendMessage") // Maps to /app/sendMessage
    @SendTo("/topic/feed")
    public String sendTestMessage(String message) {
        return message; // Broadcasts to all subscribers of /topic/feed
    }
}

