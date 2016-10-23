package io.gameoftrades.ui;

import io.gameoftrades.student34.HandelaarImpl;
import io.gameoftrades.student34.Improvements;

import javax.swing.*;

/**
 * Toont de visuele gebruikersinterface.
 * <p>
 * Let op: dit werkt alleen als je de WereldLader hebt geimplementeerd (Anders krijg je een NullPointerException).
 */
public class StudentUI {
    private static final String WESTEROS = "/kaarten/westeros-kaart.txt";
    private static final String VOORBEELD = "/kaarten/voorbeeld-kaart.txt";
    private static final String DIEPE = "/kaarten/testcases/diepe-kaart.txt";
    private static final String VERKEERDECOORDINATEN = "/kaarten/testcases/stad-verkeerde-coordinaten.txt";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
        }

        MainGui.toon(new HandelaarImpl(), VOORBEELD);
        new Improvements().improve();
    }
}
