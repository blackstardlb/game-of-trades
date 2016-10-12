package io.gameoftrades.student34;

import io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmFast;
import io.gameoftrades.student34.utils.ReflectionUtil;
import io.gameoftrades.ui.LabelPanel;
import io.gameoftrades.ui.PlanDebugPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Improvements {

    public void improve() {
        if (Frame.getFrames().length > 0 && Frame.getFrames()[0] instanceof JFrame) {
            JFrame frame = (JFrame) Frame.getFrames()[0];
            for (JPanel panel : findComponent(frame.getContentPane(), JPanel.class)) {
                for (Box box : findComponent(panel, Box.class)) {
                    for (PlanDebugPanel debugPanel : findComponent(box, PlanDebugPanel.class)) {
                        ((DefaultComboBoxModel) ReflectionUtil.get(debugPanel, "model")).addElement(new HandelsPlanAlgorithmFast());

                        for (LabelPanel labelPanel : findComponent(debugPanel, LabelPanel.class)) {
                            for (JTextField textField : findComponent(labelPanel, JTextField.class)) {
                                textField.addKeyListener(new KeyAdapter() {
                                    @Override
                                    public void keyTyped(KeyEvent e) {
                                        String text = ((textField.getText() != null ? textField.getText() : ""));
                                        if (textField.getSelectedText() != null) {
                                            text = text.substring(0, Math.max(0, textField.getSelectionStart()))
                                                   + text.substring(Math.max(0, textField.getSelectionEnd()), Math.max(0, text.length()));
                                        }
                                        if (!(text + e.getKeyChar()).matches("\\d+")) {
                                            e.consume();
                                        }
                                    }

                                });
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Container> java.util.List<T> findComponent(Container container, Class<T> cls) {
        java.util.List<T> list = new ArrayList<T>();
        for (Component comp : container.getComponents()) {
            if (comp.getClass() == cls) {
                list.add((T) comp);
            }
        }
        return list;
    }

    private <T extends Container> T findFirstComponent(Container container, Class<T> cls) {
        java.util.List<T> found = findComponent(container, cls);
        return found.size() > 0 ? found.get(0) : null;
    }

}
