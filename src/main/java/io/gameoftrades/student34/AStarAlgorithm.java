package io.gameoftrades.student34;

import io.gameoftrades.debug.AsciiArtDebugger;
import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.*;
import io.gameoftrades.student34.heuristic.DiagonalHeuristic;
import io.gameoftrades.student34.heuristic.Heuristic;
import io.gameoftrades.student34.heuristic.ManhattanHeuristic;

import java.util.*;

public class AStarAlgorithm implements SnelstePadAlgoritme, Debuggable {
    private Heuristic heuristic = new ManhattanHeuristic();

    private Debugger debug = new DummyDebugger();

    @Override
    public Pad bereken(Kaart kaart, Coordinaat start, Coordinaat eind) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        List<Node> closedList = new ArrayList<>();
        openList.add(new Node(start, start, eind, kaart, null, heuristic));
        List<Coordinaat> fixed = new ArrayList<>();

        Node currentNode = null;
        while (!openList.isEmpty()) {
            Map<Coordinaat, Double> openMap = new HashMap<>();
            for (Node node : openList) {
                openMap.put(node.getCoordinaat(), node.getfWaarde());
            }

            Map<Coordinaat, Double> closedMap = new HashMap<>();
            for (Node node : closedList) {
                closedMap.put(node.getCoordinaat(), node.getfWaarde());
            }

            this.debug.debugCoordinaten(kaart, openMap, closedMap);
            //System.out.println(openList.peek());

            currentNode = openList.poll();
            closedList.add(currentNode);

            if (currentNode.getParentNode() != null) {
                //this.debug.debugPad(kaart, start, new MyPad(currentNode));
            }

            if (currentNode.getCoordinaat().equals(eind)) {
                break;
            }

            for (Node neighbour : currentNode.getNeighbours()) {
                if (!neighbour.isTraversable() || closedList.contains(neighbour)) {
                    continue;
                }

                if (!openList.contains(neighbour)) {
                    openList.add(neighbour);
                } else {
                    for (Node original : openList) {
                        if (original.equals(neighbour)) {
                            if (neighbour.getgWaarde() < original.getgWaarde()) {
                                if (true) {
                                    System.out.println("Copy found:" + neighbour);
                                    System.out.println("Original found:" + original);

                                    original.setParentNode(neighbour.getParentNode());
                                    System.out.println("Fixed :" + original);
                                    fixed.add(original.getCoordinaat());
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        //this.debug.debugCoordinaten(kaart, fixed);
        Pad pad = new MyPad(currentNode);
        System.out.println("TotalTijd: " + pad.getTotaleTijd());
        this.debug.debugPad(kaart, start, pad);
        return pad;
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
    private double gWaarde;
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
        gWaarde = berekenGWaarde();
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

    private double hWaardepervlakteMethod() {
        int cost = 0;
        int minx = Math.min(coordinaat.getX(), coordinaatEind.getX());
        int miny = Math.min(coordinaat.getY(), coordinaatEind.getY());

        int maxx = Math.max(coordinaat.getX(), coordinaatEind.getX());
        int maxy = Math.max(coordinaat.getY(), coordinaatEind.getY());

        int count = 0;
        for (int x = minx; x < maxx; x++) {
            for (int y = miny; y < maxy; y++) {
                TerreinType terreinType = kaart.getTerreinOp(Coordinaat.op(x, y)).getTerreinType();
                if (terreinType.isToegankelijk()) {
                    cost += terreinType.getBewegingspunten();
                }
                count++;
            }
        }
        return (cost * 1.000 / count * 1.000) * 10;
    }

    public Coordinaat getCoordinaat() {
        return coordinaat;
    }

    public double gethWaarde() {
        return hWaarde;
    }

    public double getgWaarde() {
        return gWaarde;
    }

    public double getfWaarde() {
        return hWaarde + gWaarde;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
        this.gWaarde = berekenGWaarde();
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
                ", gWaarde=" + gWaarde +
                ", parentNode=" + (parentNode == null ? null : parentNode.getCoordinaat()) +
                '}';
    }
}