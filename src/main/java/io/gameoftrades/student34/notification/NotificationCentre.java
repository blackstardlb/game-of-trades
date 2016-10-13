package io.gameoftrades.student34.notification;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NotificationCentre {

    private static Queue<Notification> queue = new LinkedList<>();
    private volatile static boolean active = false;

    public static synchronized void showNotification(String message, NotificationType type) {
        if (JFrame.getFrames().length > 0) {
            queue.add(new Notification(message, type));
            nextNotification((JFrame) JFrame.getFrames()[0]);
        }
    }

    protected static void nextNotification(JFrame frame) {
        if (queue.size() > 0 && !active) {
            Notification notification = queue.poll();

            notification.show(frame);
            active = true;
        }
    }

    protected static void setActive(boolean active) {
        NotificationCentre.active = active;
    }

}