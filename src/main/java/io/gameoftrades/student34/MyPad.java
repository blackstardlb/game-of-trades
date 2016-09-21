package io.gameoftrades.student34;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Richting;
import io.gameoftrades.student34.algorithms.AStar.Node;

import java.util.Stack;

public class MyPad implements Pad {
    private final Richting[] richtingen;
    private int totaleTijd = 0;

    public MyPad(Node eindNode) {
        Stack<Node> nodes = new Stack<>();
        Node currentNode = eindNode;
        while (currentNode != null) {
            nodes.add(currentNode);
            currentNode = currentNode.getParentNode();
        }

        richtingen = new Richting[nodes.size() - 1];

        for (int i = 0; i < richtingen.length; i++) {
            Node pop = nodes.pop();
            Node peek = nodes.peek();
            richtingen[i] = Richting.tussen(pop.getCoordinaat(), peek.getCoordinaat());
            totaleTijd += peek.getTerrein().getTerreinType().getBewegingspunten();
        }
    }

    public MyPad(Richting[] richtingen, int totaleTijd) {
        this.richtingen = richtingen;
        this.totaleTijd = totaleTijd;
    }

    @Override
    public int getTotaleTijd() {
        return totaleTijd;
    }

    @Override
    public Richting[] getBewegingen() {
        return richtingen;
    }

    @Override
    public Pad omgekeerd() {
        Richting[] omgekeerd = new Richting[this.richtingen.length];
        for (int i = 0; i < omgekeerd.length; i++) {
            omgekeerd[i] = richtingen[i].omgekeerd();
        }
        return new MyPad(omgekeerd, getTotaleTijd());
    }

    @Override
    public Coordinaat volg(Coordinaat start) {
        int x = start.getX();
        int y = start.getY();
        for (Richting richting : this.richtingen) {
            switch (richting) {
                case NOORD:
                    y--;
                    break;
                case OOST:
                    x++;
                    break;
                case WEST:
                    x--;
                    break;
                case ZUID:
                    y++;
                    break;
            }
        }
        return Coordinaat.op(x, y);
    }
}