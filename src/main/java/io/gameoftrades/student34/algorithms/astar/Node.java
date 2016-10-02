package io.gameoftrades.student34.algorithms.astar;

import io.gameoftrades.model.kaart.*;
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


    public Node(Terrein terrein, Coordinaat coordinaatEind, Kaart kaart, Node parentNode, Heuristic heuristic) {
        this.heuristic = heuristic;
        this.terrein = terrein;
        this.kaart = kaart;
        this.parentNode = parentNode;
        hWaarde = heuristic.berekenH(getTerrein(), coordinaatEind);
    }

    public boolean isTraversable() {
        return getTerrein().getTerreinType().isToegankelijk();
    }

    public Terrein getTerrein() {
        return this.terrein;
    }

    public List<Node> getNeighbours(Coordinaat coordinaatEind) {
        List<Node> neighbours =
                Arrays.stream(terrein.getMogelijkeRichtingen())
                        .map(richting -> new Node(kaart.kijk(terrein, richting), coordinaatEind, kaart, this, heuristic))
                        .collect(Collectors.toList());
        return neighbours;
    }

    public Pad getPath() {
        return new PadImpl(this);
    }

    public Coordinaat getCoordinaat() {
        return terrein.getCoordinaat();
    }

    public double gethWaarde() {
        return hWaarde;
    }

    public double getgWaarde() {
        if (parentNode != null) {
            return getTerrein().getTerreinType().getBewegingspunten() + parentNode.getgWaarde();
        } else {
            return 0;
        }
    }

    public double getfWaarde() {
        return hWaarde + getgWaarde();
    }

    public List<Node> getNodePath() {
        ArrayList<Node> nodes = new ArrayList<>();
        getNodePath(this, nodes);
        return nodes;
    }

    private void getNodePath(Node startNode, List<Node> nodes) {
        if (startNode != null) {
            getNodePath(startNode.getParentNode(), nodes);
            nodes.add(startNode);
        }
    }

    public Node getParentNode() {
        return parentNode;
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