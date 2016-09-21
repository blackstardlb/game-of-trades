package io.gameoftrades.student34.algorithms.AStar.heuristics;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Terrein;

public class EuclideanHeuristic extends Heuristic {
    // d = lowest cost between 2 nodes;
    private double d = 1;

    @Override
    public double berekenH(Terrein terrein, Coordinaat coordinaatEind) {
        double dx = Math.abs(terrein.getCoordinaat().getX() - coordinaatEind.getX());
        double dy = Math.abs(terrein.getCoordinaat().getY() - coordinaatEind.getY());
        return d * Math.sqrt(dx * dx + dy * dy);
    }
}
