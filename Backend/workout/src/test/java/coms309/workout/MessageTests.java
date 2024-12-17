package coms309.workout.Chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private Message message;

    @BeforeEach
    void setUp() {
        message = new Message("senderUser", "Hello, World!", "recipientUser");
    }

    @Test
    void testConstructor() {
        assertEquals("senderUser", message.getSender());
        assertEquals("Hello, World!", message.getContent());
        assertEquals("recipientUser", message.getRecipient());
        assertNotNull(message.getSent());
    }

    @Test
    void testSettersAndGetters() {
        // Test ID
        message.setId(1L);
        assertEquals(1L, message.getId());

        // Test sender
        message.setSender("newSender");
        assertEquals("newSender", message.getSender());

        // Test content
        message.setContent("New content");
        assertEquals("New content", message.getContent());

        // Test recipient
        message.setRecipient("newRecipient");
        assertEquals("newRecipient", message.getRecipient());

        // Test sent date
        Date newDate = new Date();
        message.setSent(newDate);
        assertEquals(newDate, message.getSent());
    }

    @Test
    void testDefaultSentDate() {
        Message newMessage = new Message();
        assertNotNull(newMessage.getSent());
        assertTrue(new Date().getTime() - newMessage.getSent().getTime() < 1000, "Default date should be close to the current time");
    }
}