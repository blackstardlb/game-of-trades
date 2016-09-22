package io.gameoftrades.student34.algorithms.astar.heuristics;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Terrein;

public class DiagonalHeuristic extends Heuristic {
    // d = cost between of non diagonal step;
    private int d = 1;
    // d2 = cost of diagonal step
    private double d2 = Math.sqrt(2);

    @Override
    public double berekenH(Terrein terrein, Coordinaat coordinaatEind) {
        double dx = Math.abs(terrein.getCoordinaat().getX() - coordinaatEind.getX());
        double dy = Math.abs(terrein.getCoordinaat().getY() - coordinaatEind.getY());
        return d * (dx + dy) + (d2 - 2 * d) * Math.min(dx, dy);
    }
}
