package io.gameoftrades.student34;

import io.gameoftrades.debug.AsciiArtDebugger;
import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.*;

import java.util.*;

public class AStarAlgorithm implements SnelstePadAlgoritme, Debuggable {
    private Debugger debug = new AsciiArtDebugger();

    @Override
    public Pad bereken(Kaart kaart, Coordinaat start, Coordinaat eind) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        List<Node> closedList = new ArrayList<>();
        openList.add(new Node(start, start, eind, kaart, null));
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

            //this.debug.debugCoordinaten(kaart, openMap, closedMap);
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
        return "A* Algorithm";
    }

    @Override
    public void setDebugger(Debugger debugger) {
        this.debug = debugger;
    }
}

class MyPad implements Pad {
    private Richting[] richtingen;
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
            totaleTijd += peek.getTerreinType().getBewegingspunten();
        }
    }

    public MyPad(Richting[] richtingen) {
        this.richtingen = richtingen;
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
        System.out.println("Omgekeerd");
        Richting[] omgekeerd = new Richting[this.richtingen.length];
        for (int i = 0; i < omgekeerd.length; i++) {
            omgekeerd[i] = richtingen[i].omgekeerd();
        }
        return new MyPad(omgekeerd);
    }

    @Override
    public Coordinaat volg(Coordinaat start) {
        System.out.println("Volg");
        return null;
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


    public Node(Coordinaat coordinaat, Coordinaat coordinaatStart, Coordinaat coordinaatEind, Kaart kaart, Node parentNode) {
        this.coordinaat = coordinaat;
        this.kaart = kaart;
        this.parentNode = parentNode;
        this.coordinaatStart = coordinaatStart;
        this.coordinaatEind = coordinaatEind;
        hWaarde = berekenHWaarde();
        gWaarde = berekenGWaarde();
    }

    public boolean isTraversable() {
        return getTerreinType().isToegankelijk();
    }

    public TerreinType getTerreinType() {
        return kaart.getTerreinOp(coordinaat).getTerreinType();
    }

    public List<Node> getNeighbours() {
        List<Node> neighbours = new ArrayList<>();
        Terrein currentTerrein = kaart.getTerreinOp(coordinaat);
        for (Richting richting : currentTerrein.getMogelijkeRichtingen()) {
            Terrein kijk = kaart.kijk(currentTerrein, richting);
            neighbours.add(new Node(kijk.getCoordinaat(), coordinaatStart, coordinaatEind, kaart, this));
        }
        return neighbours;
    }

    private double berekenHWaarde() {
        return (calDistanceBetween2Points(coordinaatEind, coordinaat)) * (getTerreinType().getBewegingspunten());
    }

    private double berekenGWaarde() {
        if (parentNode != null) {
            double cost = getTerreinType().getBewegingspunten();
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

    private double calDistanceBetween2Points(Coordinaat coordinaat1, Coordinaat coordinaat2) {
        return coordinaat1.afstandTot(coordinaat2);
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