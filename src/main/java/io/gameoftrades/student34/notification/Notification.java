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

public class Notification {

	private final String message;
	private final NotificationType notificationType;
	private Timer timer;

	protected Notification(String message, NotificationType notificationType) {
		this.message = message;
		this.notificationType = notificationType;
	}

	public void show(JFrame frame) {
		if (timer == null) {
			JPanel glass = (JPanel) frame.getGlassPane();
			glass.setLayout(null);

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

			new ShowTimer(frame, notification);
		}
	}

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

	private class ShowTimer implements ActionListener {

		private final Timer timer;
		private final JFrame frame;
		private final JPanel notification;

		public ShowTimer(JFrame frame, JPanel notification) {
			this.timer = new Timer(5, this);
			this.frame = frame;
			this.notification = notification;

			this.timer.setInitialDelay(1);
			this.timer.start();
			Notification.this.timer = this.timer;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (timer.isRunning()) {
				if (notification.getY() >= 5) {
					this.timer.stop();
					new StopTimer(frame, notification);
					return;
				}
				notification.setLocation(notification.getX(), notification.getY() + 2);
			}
		}

	}

	private class StopTimer implements ActionListener {

		private final Timer timer;
		private final JFrame frame;
		private final JPanel notification;

		public StopTimer(JFrame frame, JPanel notification) {
			this.timer = new Timer(5, this);
			this.frame = frame;
			this.notification = notification;

			this.timer.setInitialDelay(4000);
			this.timer.start();
			Notification.this.timer = this.timer;
		}

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