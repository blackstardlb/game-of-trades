package io.gameoftrades.student34.notification;

import java.awt.Color;

/**
 * Dit zijn de type notificaties die je kunt weergeven. Deze types geven aan wat de layout van de notificatie moet zijn
 * door aan te geven wat de achtergrond-, de border- en de tekstkleur moet zijn.
 *
 * @see Notification
 * @see NotificationCentre
 */
public enum NotificationType {

    /**
     * ERROR moet worden gebruikt als je een notificatie wilt laten zien die een foutmelding aangeeft. De notificatie
     * wordt dan rood.
     */
    ERROR(new Color(242, 222, 222), new Color(235, 204, 209), new Color(196, 68, 66)),
    /**
     * SUCCESS moet worden gebruikt als je een notificatie wilt laten zien die een 'success' melding geeft. De notificatie
     * wordt dan groen.
     */
    SUCCESS(new Color(223, 240, 216), new Color(214, 233, 198), new Color(60, 118, 61));

    private final Color background, border, textColor;

    private NotificationType(Color background, Color border, Color textColor) {
        this.background = background;
        this.border = border;
        this.textColor = textColor;
    }

    public Color getBackground() {
        return background;
    }

    public Color getBorder() {
        return border;
    }

    public Color getTextColor() {
        return textColor;
    }

}