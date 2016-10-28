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

/**
 * In deze class maken aantal verbeteringen aan het huidige programma toe. Wij hadden een aantal dingen gevonden die we
 * aan wilden passen, dus hebben we dat in deze class gedaan
 *
 * @see #improve()
 */
public class Improvements {

    /**
     * <p>Deze method voegt een aantal verbeteringen aan het huidige programma toe.</p>
     * <p>Een van de dingen die we hebben aangepast is dat je in de tekstvelden van het handelsplan algoritme cijfers moest
     * invullen, maar je kon ook gewoon letters en andere karakters invullen. Wat wij hebben gedaan is een {@link java.awt.event.KeyListener}
     * toegevoegd die ervoor zorgt dat je alleen cijfers in de tekstvelden mag invullen</p>
     * <p>Verder hebben we ook twee {@link io.gameoftrades.model.algoritme.HandelsplanAlgoritme handelsplan algoritmes}, dus
     * voegen we onze andere algoritme handmatig aan het dropdown menu toe, zodat je deze later in het programma kunt selecteren</p>
     *
     * @see Frame
     * @see JFrame
     * @see java.awt.event.KeyListener
     * @see JTextField
     * @see #findComponent(Container, Class)
     */
    @SuppressWarnings("unchecked")
    public void improve() {
        // Kijk of er wel een frame actief is
        if (Frame.getFrames().length > 0 && Frame.getFrames()[0] instanceof JFrame) {
            // Pak het eerste actieve frame
            JFrame frame = (JFrame) Frame.getFrames()[0];
            for (JPanel panel : findComponent(frame.getContentPane(), JPanel.class)) {
                for (Box box : findComponent(panel, Box.class)) {
                    for (PlanDebugPanel debugPanel : findComponent(box, PlanDebugPanel.class)) {
                        ((DefaultComboBoxModel) ReflectionUtil.get(debugPanel, "model")).addElement(new HandelsPlanAlgorithmFast());

                        for (LabelPanel labelPanel : findComponent(debugPanel, LabelPanel.class)) {
                            for (JTextField textField : findComponent(labelPanel, JTextField.class)) {
                                // Voeg de key listener toe
                                textField.addKeyListener(new KeyAdapter() {
                                    @Override
                                    public void keyTyped(KeyEvent e) {
                                        String text = ((textField.getText() != null ? textField.getText() : ""));
                                        // Kijk of de gebruiker een bepaald gedeelte van de tekst in het tekstveld heeft geselecteerd.
                                        if (textField.getSelectedText() != null) {
                                            text = text.substring(0, Math.max(0, textField.getSelectionStart()))
                                                    + text.substring(Math.max(0, textField.getSelectionEnd()), Math.max(0, text.length()));
                                        }

                                        // Kijk of de text + het ingetypde karakter GEEEN nummer is
                                        if (!(text + e.getKeyChar()).matches("\\d+")) {
                                            // Consume de event (cancel). Dit zorgt ervoor dat het karakter niet wordt ingetyped
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

    /**
     * Deze method zoekt door alle child components van een {@link Container} en geeft een lijst van alle child components
     * die van een gegevens class zijn.
     *
     * @param container De container waarin gezocht wordt
     * @param cls       De class die een child component moet matchen om aan de lijst toegevoegd te worden
     * @param <T>       De type container die de child components moeten matchen
     * @return Geeft een lijst van child components van een bepaalde class terug.
     */
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
}
