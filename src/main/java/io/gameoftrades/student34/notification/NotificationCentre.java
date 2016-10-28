package io.gameoftrades.student34.notification;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * In deze class kun je notificaties op het scherm laten zien. De notificaties worden in een wachtrij gezet en in volgorde
 * op het scherm weergegeven. De notificaties zouden thread-safe moeten zijn, dus het maakt niet uit van welke thread je een
 * notificatie probeert te laten zien.
 */
public class NotificationCentre {

    /**
     * De wachtrij voor alle notificaties
     */
    private static Queue<Notification> queue = new LinkedList<>();
    /**
     * Deze boolean geeft aan of er al een notificatie actief is.
     */
    private volatile static boolean active = false;

    /**
     * Deze method zal een notificatie aan de wachtrij toevoegen.
     *
     * @param message Het bericht dat in de notificatie moet staan
     * @param type    Het type notificatie
     * @see NotificationType
     * @see Notification
     * @see #queue
     */
    public static synchronized void showNotification(String message, NotificationType type) {
        if (JFrame.getFrames().length > 0) {
            queue.add(new Notification(message, type));
            nextNotification((JFrame) JFrame.getFrames()[0]);
        }
    }

    /**
     * Deze method laat de eerstvolgende notificatie in de wachtrij op het scherm zien (als die er is) als op dat moment
     * nog geen notificatie actief is.
     *
     * @param frame Het frame waar de notificatie op wordt weergegeven
     * @see JFrame
     * @see Notification#show(JFrame)
     */
    protected synchronized static void nextNotification(JFrame frame) {
        if (queue.size() > 0 && !active) {
            Notification notification = queue.poll();

            notification.show(frame);
            setActive(true);
        }
    }

    /**
     * Deze method geeft aan of er een notificatie actief is of niet.
     *
     * @param active De boolean die aan geeft of een notificatie actief is of niet
     */
    protected static void setActive(boolean active) {
        NotificationCentre.active = active;
    }

}