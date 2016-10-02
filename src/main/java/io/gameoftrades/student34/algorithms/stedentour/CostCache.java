package io.gameoftrades.student34.algorithms.stedentour;

import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.student34.algorithms.astar.AStarAlgorithm;

import java.util.HashMap;
import java.util.Map;

public class CostCache {
    private static final Map<DoubleMapKey<Stad>, Integer> costs = new HashMap<>();
    private static final SnelstePadAlgoritme snelstePadAlgoritme = new AStarAlgorithm();

    public static int getCost(Kaart kaart, Stad stad1, Stad stad2) {
        DoubleMapKey<Stad> key = new DoubleMapKey<>(stad1, stad2);
        if (costs.containsKey(key)) {
            return costs.get(key);
        } else {
            int cost = snelstePadAlgoritme.bereken(kaart, stad1.getCoordinaat(), stad2.getCoordinaat()).getTotaleTijd();
            costs.put(key, cost);
            return cost;
        }
    }
}
