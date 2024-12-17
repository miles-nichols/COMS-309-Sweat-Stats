package coms309.workout.Chat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
@Profile("!test")

@Configuration
@EnableWebSocket
public class ChatSocketConfig{
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        // Check if ServerContainer is available
        if (isWebSocketEnvironment()) {
            return new ServerEndpointExporter();
        }
        return null; // Avoid bean creation in unsupported environments
    }
    private boolean isWebSocketEnvironment() {
        try {
            Class.forName("jakarta.websocket.server.ServerContainer");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

}
