package io.gameoftrades.student34.algorithms.astar.heuristics;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Terrein;

public class MyHeuristic extends Heuristic {
    @Override
    public double berekenH(Terrein terrein, Coordinaat coordinaatEind) {
        return (calDistanceBetween2Points(coordinaatEind, terrein.getCoordinaat())) * (terrein.getTerreinType().getBewegingspunten());
    }

    private double calDistanceBetween2Points(Coordinaat coordinaat1, Coordinaat coordinaat2) {
        return coordinaat1.afstandTot(coordinaat2);
    }
}
