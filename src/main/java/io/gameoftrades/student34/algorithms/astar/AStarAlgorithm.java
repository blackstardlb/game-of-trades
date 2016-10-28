package io.gameoftrades.student34.algorithms.astar;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.student34.NullPad;
import io.gameoftrades.student34.PadImpl;
import io.gameoftrades.student34.algorithms.astar.heuristics.Heuristic;
import io.gameoftrades.student34.algorithms.astar.heuristics.ManhattanHeuristic;
import io.gameoftrades.student34.notification.NotificationCentre;
import io.gameoftrades.student34.notification.NotificationType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Deze class wordt gebruikt om de snelste route tussen twee coordinaaten te berekenen.
 */
public class AStarAlgorithm implements SnelstePadAlgoritme, Debuggable {

    private Heuristic heuristic = new ManhattanHeuristic();
    private Debugger debug = new DummyDebugger();
    private final boolean moetDebuggen;

    /**
     * @param moetDebuggen True als je visuel will debuggen.
     */
    public AStarAlgorithm(boolean moetDebuggen) {
        this.moetDebuggen = moetDebuggen;
    }

    /**
     * Deze methode wordt gebruikt om de snelste route tussen twee coordinaaten te berekenen.
     *
     * @param kaart De {@link Kaart}.
     * @param start De begin {@link Coordinaat}.
     * @param eind  Het eind {@link Coordinaat}.
     * @return De berekend snelste {@link Pad} tussen het begin en eind {@link Coordinaat}.
     */
    @Override
    public Pad bereken(Kaart kaart, Coordinaat start, Coordinaat eind) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        List<Node> closedList = new ArrayList<>();
        openList.add(new Node(kaart.getTerreinOp(start), eind, kaart, null, heuristic));

        Node currentNode;
        while (!openList.isEmpty()) {
            if (moetDebuggen) {
                this.deBugCurrentPath(openList.peek(), kaart, start);
            }
            currentNode = openList.poll();
            closedList.add(currentNode);

            if (currentNode.getCoordinaat().equals(eind)) {
                Pad pad = currentNode.getPath();

                if (moetDebuggen) {
                    this.deBugOpenCloseLists(openList, closedList, kaart, Node::getfWaarde);
                }
                return pad;
            }

            for (Node neighbour : currentNode.getNeighbours(eind)) {
                if (!neighbour.isTraversable() || closedList.contains(neighbour)) {
                    continue;
                }

                Optional<Node> nodeOptional = openList.stream().filter(node -> node.equals(neighbour)).findAny();
                if (nodeOptional.isPresent()) {
                    Node original = nodeOptional.get();
                    if (original.getPath().getTotaleTijd() > neighbour.getPath().getTotaleTijd()) { // Verander de pad van de neighbour als de nieuwe pad better is.
                        openList.remove(original);
                        openList.add(neighbour);
                    }
                } else {
                    openList.add(neighbour);
                }
            }
        }

        if (moetDebuggen) {
            NotificationCentre.showNotification("Er is geen mogelijke route tussen deze twee steden!", NotificationType.ERROR);
        }
        //throw new RuntimeException("Path not found between " + start + " and " + eind);
        return new NullPad();
    }

    /**
     * Deze methode wordt gebruikt om de open  en closed lists visuel te debuggen.
     *
     * @param openList   De open list.
     * @param closedList De closed list.
     * @param kaart      De {@link Kaart}.
     * @param function   De function om van een node een double te maken.
     */
    private void deBugOpenCloseLists(Collection<Node> openList, Collection<Node> closedList, Kaart kaart, Function<Node, Double> function) {
        Map<Coordinaat, Double> openMap = openList.stream().collect(Collectors.toMap(Node::getCoordinaat, function));

        Map<Coordinaat, Double> closedMap = closedList.stream().collect(Collectors.toMap(Node::getCoordinaat, function));

        this.debug.debugCoordinaten(kaart, openMap, closedMap);
    }

    /**
     * Deze methode wordt gebruikt om de pad van een begin coordinaat tot een bepaalde node te visuel debuggen.
     *
     * @param currentNode De node van waar we willen debuggen.
     * @param kaart       De {@link Kaart}.
     * @param start       De begin {@link Coordinaat}.
     */
    private void deBugCurrentPath(Node currentNode, Kaart kaart, Coordinaat start) {
        if (currentNode != null) {
            this.debug.debugPad(kaart, start, new PadImpl(currentNode));
        }
    }

    @Override
    public String toString() {
        return "A* Algorithm with " + this.heuristic;
    }

    /**
     * @param debugger Set the {@link Debugger} die we willen gebruiken om deze Algorithme visuel te debuggen.
     */
    @Override
    public void setDebugger(Debugger debugger) {
        this.debug = debugger;
    }
}

