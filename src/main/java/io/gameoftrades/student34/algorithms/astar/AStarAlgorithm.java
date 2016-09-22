package io.gameoftrades.student34.algorithms.astar;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.student34.MyPad;
import io.gameoftrades.student34.algorithms.astar.heuristics.Heuristic;
import io.gameoftrades.student34.algorithms.astar.heuristics.ManhattanHeuristic;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AStarAlgorithm implements SnelstePadAlgoritme, Debuggable {
    private Heuristic heuristic = new ManhattanHeuristic();
    private Debugger debug = new DummyDebugger();

    @Override
    public Pad bereken(Kaart kaart, Coordinaat start, Coordinaat eind) {
//        long startmillis = System.currentTimeMillis();
        PriorityQueue<Node> openList = new PriorityQueue<>();
        List<Node> closedList = new ArrayList<>();
        openList.add(new Node(kaart.getTerreinOp(start), eind, kaart, null, heuristic));

        Node currentNode = null;
        while (!openList.isEmpty()) {
            this.deBugCurrentPath(openList.peek(), kaart, start);
            currentNode = openList.poll();
            closedList.add(currentNode);

            if (currentNode.getCoordinaat().equals(eind)) {
                break;
            }

            for (Node neighbour : currentNode.getNeighbours(eind)) {
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
            /*System.out.println("Nodes Evaluated: " + (openList.size() + closedList.size()) + " of " + (kaart.getBreedte() * kaart.getHoogte()));
            System.out.println("TotaleTijd: " + pad.getTotaleTijd());
            System.out.println("Millis: " + (System.currentTimeMillis() - startmillis));*/
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
        if (currentNode != null) {
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

