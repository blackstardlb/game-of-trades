package io.gameoftrades.student34.notification;

import java.awt.Color;

public enum NotificationType {

    ERROR(new Color(242, 222, 222), new Color(235, 204, 209), new Color(196, 68, 66)),
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