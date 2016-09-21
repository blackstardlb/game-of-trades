package io.gameoftrades.student34.heuristic;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Terrein;

public class ManhattanHeuristic extends Heuristic {
    // d = lowest cost between 2 nodes;
    private int d = 1;

    @Override
    public double berekenH(Terrein terrein, Coordinaat coordinaatEind) {
        double dx = Math.abs(terrein.getCoordinaat().getX() - coordinaatEind.getX());
        double dy = Math.abs(terrein.getCoordinaat().getY() - coordinaatEind.getY());
        return d * (dx + dy);
    }
}
