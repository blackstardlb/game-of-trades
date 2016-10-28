package io.gameoftrades.student34.algorithms.astar;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Terrein;
import io.gameoftrades.student34.PadImpl;
import io.gameoftrades.student34.algorithms.astar.heuristics.Heuristic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Node implements Comparable<Node> {
    private final Terrein terrein;
    private final double hWaarde;
    private Node parentNode;
    private final Kaart kaart;
    private Heuristic heuristic;


    /**
     * @param terrein        De {@link Terrein} waarop deze node zich bevindt.
     * @param coordinaatEind De eind {@link Coordinaat} dat we naartoe willen.
     * @param kaart          De {@link Kaart}.
     * @param parentNode     De parent {@link Node}.
     * @param heuristic      De {@link Heuristic} die gebruikt zal worden om de H waarde te berekenen.
     */
    public Node(Terrein terrein, Coordinaat coordinaatEind, Kaart kaart, Node parentNode, Heuristic heuristic) {
        this.heuristic = heuristic;
        this.terrein = terrein;
        this.kaart = kaart;
        this.parentNode = parentNode;
        hWaarde = heuristic.berekenH(getTerrein(), coordinaatEind);
    }

    /**
     * @return True als de node toegankelijk is.
     */
    public boolean isTraversable() {
        return getTerrein().getTerreinType().isToegankelijk();
    }

    /**
     * @return De {@link Terrein} waarop deze node zich bevindt.
     */
    public Terrein getTerrein() {
        return this.terrein;
    }

    /**
     * @param coordinaatEind De eind {@link Coordinaat}.
     * @return Een lijst van alle toegankelijk {@link Node nodes} die buren van deze {@link Node} zijn.
     */
    public List<Node> getNeighbours(Coordinaat coordinaatEind) {
        List<Node> neighbours =
                Arrays.stream(terrein.getMogelijkeRichtingen())
                        .map(richting -> new Node(kaart.kijk(terrein, richting), coordinaatEind, kaart, this, heuristic))
                        .collect(Collectors.toList());
        return neighbours;
    }

    /**
     * @return De {@link Pad} van de begin Coordinaat tot en met deze {@link Node}.
     */
    public Pad getPath() {
        return new PadImpl(this);
    }

    /**
     * @return De {@link Coordinaat} van deze {@link Node}.
     */
    public Coordinaat getCoordinaat() {
        return terrein.getCoordinaat();
    }

    /**
     * @return De H waarde van deze {@link Node}. De H waarde is de minimale afstand van deze {@link Node} tot het eindpunt.
     */
    public double gethWaarde() {
        return hWaarde;
    }

    /**
     * @return De G waarde van deze {@link Node}. De G waarde is de afstand van deze {@link Node} tot het beginpunt.
     */
    public double getgWaarde() {
        if (parentNode != null) {
            return getTerrein().getTerreinType().getBewegingspunten() + parentNode.getgWaarde();
        } else {
            return 0;
        }
    }

    /**
     * @return De F waarde van deze {@link Node}. De F waarde is de H Waarde + de G Waarde.
     */
    public double getfWaarde() {
        return hWaarde + getgWaarde();
    }

    /**
     * @return een lijst van alle {@link Node nodes} van de begin node tot deze {@link Node}.
     */
    public List<Node> getNodePath() {
        ArrayList<Node> nodes = new ArrayList<>();
        if (parentNode != null) {
            nodes.addAll(parentNode.getNodePath());
        }
        nodes.add(this);
        return nodes;
    }

    @Override
    public int compareTo(Node node) {
        int val = Double.compare(getfWaarde(), node.getfWaarde());
        if (val == 0) {
            val = Double.compare(gethWaarde(), node.gethWaarde());
        }
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Node) {
            Node node2 = (Node) o;
            return this.getTerrein().equals(node2.getTerrein());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "terrein=" + terrein +
                ", fWaarde=" + getfWaarde() +
                ", hWaarde=" + hWaarde +
                ", gWaarde=" + getgWaarde() +
                ", parentNode=" + (parentNode == null ? null : parentNode.getTerrein().getCoordinaat()) +
                '}';
    }
}