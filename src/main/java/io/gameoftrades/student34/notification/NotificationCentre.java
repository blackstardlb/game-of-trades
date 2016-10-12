package io.gameoftrades.student34.notification;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationCentre {

    private static List<Notification> queue = new ArrayList<Notification>();
    private volatile static boolean active = false;

    public static synchronized void showNotification(String message, NotificationType type) {
        if (JFrame.getFrames().length > 0) {
            queue.add(new Notification(message, type));
            nextNotification((JFrame) JFrame.getFrames()[0]);
        }
    }

    protected static void nextNotification(JFrame frame) {
        if (queue.size() > 0 && !active) {
            Notification notification = queue.get(0);

            notification.show(frame);
            active = true;

            queue.remove(0);
        }
    }

    protected static void setActive(boolean active) {
        NotificationCentre.active = active;
    }

}