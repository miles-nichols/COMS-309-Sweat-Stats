package coms309.workout.ActivityFeed;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ActivityFeedHandler activityFeedHandler;

    public WebSocketConfig(ActivityFeedHandler activityFeedHandler) {
        this.activityFeedHandler = activityFeedHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(activityFeedHandler, "/ws").setAllowedOrigins("*");
    }
}
