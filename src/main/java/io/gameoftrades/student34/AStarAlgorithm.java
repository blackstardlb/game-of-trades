package io.gameoftrades.student34;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.*;
import io.gameoftrades.student34.heuristic.Heuristic;
import io.gameoftrades.student34.heuristic.ManhattanHeuristic;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AStarAlgorithm implements SnelstePadAlgoritme, Debuggable {
    private Heuristic heuristic = new ManhattanHeuristic();
    private Debugger debug = new DummyDebugger();

    @Override
    public Pad bereken(Kaart kaart, Coordinaat start, Coordinaat eind) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        List<Node> closedList = new ArrayList<>();
        openList.add(new Node(start, start, eind, kaart, null, heuristic));

        Node currentNode = null;
        while (!openList.isEmpty()) {
            currentNode = openList.poll();
            closedList.add(currentNode);

            if (currentNode.getCoordinaat().equals(eind)) {
                break;
            }

            for (Node neighbour : currentNode.getNeighbours()) {
                if (!neighbour.isTraversable() || closedList.contains(neighbour)) {
                    continue;
                }

                Optional<Node> nodeOptional = openList.stream().filter(node -> node.equals(neighbour)).findAny();
                if (nodeOptional.isPresent()) {
                    Node original = nodeOptional.get();
                    if (original.getPath().getTotaleTijd() > neighbour.getPath().getTotaleTijd()) {
                        openList.remove(original);
                        openList.add(neighbour);
                    }
                } else {
                    openList.add(neighbour);
                }
            }
        }
        Pad pad = null;
        if (currentNode != null) {
            pad = currentNode.getPath();
            System.out.println("Nodes Evaluated: " + (openList.size() + closedList.size()) + " of " + (kaart.getBreedte() * kaart.getHoogte()));
            System.out.println("TotaleTijd: " + pad.getTotaleTijd());
            this.deBugOpenCloseLists(openList, closedList, kaart, Node::getfWaarde);
            this.debug.debugPad(kaart, start, pad);
        }
        return pad;
    }

    private void deBugOpenCloseLists(Collection<Node> openList, Collection<Node> closedList, Kaart kaart, Function<Node, Double> function) {
        Map<Coordinaat, Double> openMap = openList.stream().collect(Collectors.toMap(Node::getCoordinaat, function));

        Map<Coordinaat, Double> closedMap = closedList.stream().collect(Collectors.toMap(Node::getCoordinaat, function));

        this.debug.debugCoordinaten(kaart, openMap, closedMap);
    }

    private void deBugCurrentPath(Node currentNode, Kaart kaart, Coordinaat start) {
        if (currentNode != null && currentNode.getParentNode() != null) {
            this.debug.debugPad(kaart, start, new MyPad(currentNode));
        }
    }

    @Override
    public String toString() {
        return "A* Algorithm with " + this.heuristic;
    }

    @Override
    public void setDebugger(Debugger debugger) {
        this.debug = debugger;
    }
}

class Node implements Comparable<Node> {
    private final Coordinaat coordinaat;
    private final double hWaarde;
    private Node parentNode;
    private final Kaart kaart;
    private final Coordinaat coordinaatStart;
    private final Coordinaat coordinaatEind;
    private Heuristic heuristic;


    public Node(Coordinaat coordinaat, Coordinaat coordinaatStart, Coordinaat coordinaatEind, Kaart kaart, Node parentNode, Heuristic heuristic) {
        this.heuristic = heuristic;
        this.coordinaat = coordinaat;
        this.kaart = kaart;
        this.parentNode = parentNode;
        this.coordinaatStart = coordinaatStart;
        this.coordinaatEind = coordinaatEind;
        hWaarde = berekenHWaarde();
    }

    public boolean isTraversable() {
        return getTerrein().getTerreinType().isToegankelijk();
    }

    public Terrein getTerrein() {
        return kaart.getTerreinOp(coordinaat);
    }

    public List<Node> getNeighbours() {
        List<Node> neighbours = new ArrayList<>();
        Terrein currentTerrein = kaart.getTerreinOp(coordinaat);
        for (Richting richting : currentTerrein.getMogelijkeRichtingen()) {
            Terrein kijk = kaart.kijk(currentTerrein, richting);
            neighbours.add(new Node(kijk.getCoordinaat(), coordinaatStart, coordinaatEind, kaart, this, heuristic));
        }
        return neighbours;
    }

    private double berekenHWaarde() {
        return heuristic.berekenH(getTerrein(), coordinaatEind);
    }

    private double berekenGWaarde() {
        if (parentNode != null) {
            double cost = getTerrein().getTerreinType().getBewegingspunten();
            cost += parentNode.getgWaarde();
            return cost;
        } else {
            return 0;
        }
    }

    public Pad getPath() {
        if (parentNode != null) {
            return new MyPad(this);
        } else {
            return null;
        }
    }

    public Coordinaat getCoordinaat() {
        return coordinaat;
    }

    public double gethWaarde() {
        return hWaarde;
    }

    public double getgWaarde() {
        return berekenGWaarde();
    }

    public double getfWaarde() {
        return hWaarde + getgWaarde();
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
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
            return this.coordinaat.equals(node2.coordinaat);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "coordinaat=" + coordinaat +
                ", fWaarde=" + getfWaarde() +
                ", hWaarde=" + hWaarde +
                ", gWaarde=" + getgWaarde() +
                ", parentNode=" + (parentNode == null ? null : parentNode.getCoordinaat()) +
                '}';
    }
}