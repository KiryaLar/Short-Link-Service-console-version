package ru.larkin.notifications;

import java.util.UUID;

public class NotificationService {
    public static void notifyUser(UUID userId, String message) {
        System.out.println("Notification for user " + userId + ":\n" + message);
    }
}
