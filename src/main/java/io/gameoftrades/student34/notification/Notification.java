package io.gameoftrades.student34.notification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 * Dit is de class die alle animaties van de notificatie doet. Hij start is de {@link ShowTimer} die de notificatie
 * op het scherm laat zien, waarna de {@link StopTimer} wordt gestart die de notificatie weer zal verwijderen
 *
 * @see NotificationCentre
 */
public class Notification {

    /**
     * Het bericht dat in de notificatie wordt laten zien
     */
    private final String message;
    /**
     * Het type notificatie
     *
     * @see NotificationType
     */
    private final NotificationType notificationType;
    /**
     * Dit is de animatie timer. Deze timer wordt gebruikt voor de {@link ShowTimer} en de {@link StopTimer}
     *
     * @see ShowTimer
     * @see StopTimer
     */
    private Timer timer;

    protected Notification(String message, NotificationType notificationType) {
        this.message = message;
        this.notificationType = notificationType;
    }

    /**
     * Deze method maakt een panel met de layout for de notificatie aan, en start de timer die deze notificatie op het
     * scherm laat zien.
     *
     * @param frame De JFrame waar de notificatie op wordt weergegeven
     * @see ShowTimer
     */
    public void show(JFrame frame) {
        if (timer == null) {
            JPanel glass = (JPanel) frame.getGlassPane();
            glass.setLayout(null);

            // De panel waar de notificatie op komt te staans
            JPanel notification = new JPanel();
            int width = (int) ((double) frame.getContentPane().getWidth() * 0.7);
            notification.setSize(width, 50);
            notification.setLocation((frame.getContentPane().getWidth() / 2) - (notification.getWidth() / 2), -50);
            notification.setBorder(BorderFactory.createLineBorder(notificationType.getBorder(), 1));
            notification.setBackground(notificationType.getBackground());

            JTextArea area = new JTextArea(message);
            area.setEditable(false);
            area.setLineWrap(true);
            area.setBorder(BorderFactory.createEmptyBorder());
            area.setWrapStyleWord(true);
            area.setOpaque(false);
            area.setForeground(notificationType.getTextColor());
            area.setLocation(20, 5);
            area.setSize(notification.getWidth() - 20, notification.getHeight());

            notification.add(area);

            // Voeg een listener die de notificatie panel resized als de window wordt ge-resized
            glass.addComponentListener(new ComponentAdapter() {

                @Override
                public void componentResized(ComponentEvent e) {
                    int width = (int) ((double) frame.getContentPane().getWidth() * 0.7);
                    notification.setSize(width, 50);
                    notification.setLocation((frame.getContentPane().getWidth() / 2) - (notification.getWidth() / 2),
                            notification.getY());
                    area.setSize(notification.getWidth() - 20, notification.getHeight());
                }

            });

            glass.add(notification);
            glass.setVisible(true);
            glass.repaint();

            // Start de animatie die de notificatie op het scherm laat zien
            new ShowTimer(frame, notification);
        }
    }

    /**
     * Deze method forceert de notificatie om te stoppen. Hij stop de animatie timer en verwijdert de notificatie panel
     * van de JFrame. Deze method zal ook aangeven aan de {@link NotificationCentre} dat de animatie van deze notificatie
     * afgelopen is, en zal proberen de volgende notificatie in de wachtrij te starten.
     *
     * @param frame De frame waar de notificatie op staat
     * @see JFrame
     * @see NotificationCentre
     * @see NotificationCentre#setActive(boolean)
     * @see NotificationCentre#nextNotification(JFrame)
     * @see #timer
     */
    public void stop(JFrame frame) {
        if (timer != null) {
            this.timer.stop();
            JPanel glass = (JPanel) frame.getGlassPane();
            glass.removeAll();
            glass.setVisible(false);
            for (ComponentListener adapter : glass.getListeners(ComponentListener.class)) {
                glass.removeComponentListener(adapter);
            }
            NotificationCentre.setActive(false);
            NotificationCentre.nextNotification(frame);
        }
    }

    /**
     * Dit is de animatie timer die de notificatie op het scherm laat zien. De notificatiepanel begeeft zich eerst uit het
     * scherm, en deze timer zal de notificatiepanel geleidelijk naar benden schuiven totdat hij volledig zichtbaar is.
     *
     * @see Timer
     * @see #timer
     * @see ActionListener
     */
    private class ShowTimer implements ActionListener {

        /**
         * De frame waar de notificatie op wordt laten zien
         */
        private final JFrame frame;
        /**
         * De notificatie panel die op het scherm wordt weergegeven
         */
        private final JPanel notification;

        /**
         * Maakt de nieuwe timer en start deze timer.
         *
         * @param frame        De frame waar de notificatie op wordt laten zien
         * @param notification De notificatie panel die op het scherm wordt weergegeven
         * @see Timer
         */
        public ShowTimer(JFrame frame, JPanel notification) {
            if (timer != null) {
                timer.stop();
            }
            timer = new Timer(5, this);
            this.frame = frame;
            this.notification = notification;

            timer.setInitialDelay(1);
            timer.start();
        }

        /**
         * Deze method zal door de timer elke aantal milliseconde (kijk naar {@link Timer#Timer(int, ActionListener)}
         * hoe je deze aantal milliseconde moet instellen) worden aangeroepen. We zullen in deze method geleidelijk de
         * notificatie panel op het scherm laten zien. Zodra de panel volledig te zien is, zal de {@link StopTimer}
         * worden gestart.
         *
         * @param e De ActionEvent die wordt meegegeven door de Timer
         * @see ActionEvent
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (timer.isRunning()) {
                if (notification.getY() >= 5) {
                    timer.stop();
                    timer = null;
                    new StopTimer(frame, notification);
                    return;
                }
                notification.setLocation(notification.getX(), notification.getY() + 2);
            }
        }

    }

    /**
     * Deze class zal een animatie timer starten die de notificatiepanel van het scherm zal verwijderen. Het zal geleidelijk
     * de panel naar boven schuiven totdat deze volledig uit het scherm is verdwenen. Zodra de notificatie panel volledige uit
     * het scherm staat, zal deze verwijdert worden van de frame.
     *
     * @see Timer
     * @see #timer
     * @see ActionListener
     */
    private class StopTimer implements ActionListener {

        /**
         * De frame waar de notificatiepanel van wordt verwijdert
         */
        private final JFrame frame;
        /**
         * De notificatiepanel die van het scherm wordt verwijdert
         */
        private final JPanel notification;

        /**
         * Maakt de nieuwe timer en start deze timer. Eerst wacht de timer 4 seconden voordat hij start met het wegschuiven
         * van de notificatiepanel. Dit geeft de gebruiker de tijd om de notificatie te lezen.
         *
         * @param frame        De frame waar de notificatiepanel van wordt verwijdert
         * @param notification De notificatiepanel die van het scherm wordt verwijdert
         * @see Timer
         */
        public StopTimer(JFrame frame, JPanel notification) {
            if (timer != null) {
                timer.stop();
            }
            timer = new Timer(5, this);
            this.frame = frame;
            this.notification = notification;

            timer.setInitialDelay(4000);
            timer.start();
        }

        /**
         * Deze method zal door de timer elke aantal milliseconde (kijk naar {@link Timer#Timer(int, ActionListener)}
         * hoe je deze aantal milliseconde moet instellen) worden aangeroepen. We gaan hierin geleidelijk de notificatiepanel
         * naar boven schuiven totdat hij volledig uit het scherm is verdwenen. Hierna wordt de panel volledig van de frame
         * verwijdert.
         *
         * @param e De ActionEvent die wordt meegegeven door de Timer
         * @see Timer
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (timer.isRunning()) {
                if (notification.getY() <= -notification.getHeight()) {
                    stop(frame);
                    return;
                }
                notification.setLocation(notification.getX(), notification.getY() - 2);
            }
        }
    }

}