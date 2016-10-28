package io.gameoftrades.student34;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Richting;
import io.gameoftrades.student34.algorithms.astar.Node;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Onze implementatie van {@link Pad}. Er wordt eerste een lijst met {@link Node nodes} berekent door het {@link io.gameoftrades.student34.algorithms.astar.AStarAlgorithm}
 * En deze wordt in de {@link #PadImpl(Node) constructor} omgezet naar een array van {@link Richting richtingen}.
 *
 * @see Pad
 * @see io.gameoftrades.student34.algorithms.astar.AStarAlgorithm
 * @see Node
 */
public class PadImpl implements Pad {
    private final Richting[] richtingen;
    private int totaleTijd = 0;

    /**
     * Zet het berekende pad van {@link Node nodes} om in een array van {@link Richting richtingen}
     *
     * @param eindNode De laatste node van het pad, vanuit deze node kun je het totale pad krijgen.
     */
    public PadImpl(Node eindNode) {
        // Maak een lijst van alle nodes die in het pad zitten
        Queue<Node> nodes = new LinkedList<>(eindNode.getNodePath());
        richtingen = new Richting[nodes.size() - 1];

        // Loop door alle nodes heen
        for (int i = 0; i < richtingen.length; i++) {
            // De huidige node
            Node pop = nodes.poll();
            // De volgende node
            Node peek = nodes.peek();
            // Kijk wat de richting tussen de huidige en de volgende node is
            richtingen[i] = Richting.tussen(pop.getCoordinaat(), peek.getCoordinaat());
            totaleTijd += peek.getTerrein().getTerreinType().getBewegingspunten();
        }
    }

    public PadImpl(Richting[] richtingen, int totaleTijd) {
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
        for (int i = omgekeerd.length - 1; i >= 0; i--) {
            omgekeerd[(omgekeerd.length - 1) - i] = richtingen[i].omgekeerd();
        }
        return new PadImpl(omgekeerd, getTotaleTijd());
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