package io.gameoftrades.student34.algorithms.stedentour;

import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.student34.algorithms.astar.AStarAlgorithm;

import java.util.HashMap;
import java.util.Map;

public class CostCache {

    private static final Map<DoubleMapKey<Stad>, Pad> costs = new HashMap<>();
    private static final SnelstePadAlgoritme snelstePadAlgoritme = new AStarAlgorithm(false);

    public static Pad getPath(Kaart kaart, Stad stad1, Stad stad2) {
        DoubleMapKey<Stad> key = new DoubleMapKey<>(stad1, stad2);
        DoubleMapKey<Stad> key2 = new DoubleMapKey<>(stad2, stad1);
        if (costs.containsKey(key)) {
            return costs.get(key);
        } else if (costs.containsKey(key2)) {
            return costs.get(key2);
        } else {
            Pad pad = snelstePadAlgoritme.bereken(kaart, stad1.getCoordinaat(), stad2.getCoordinaat());
            costs.put(key, pad);
            Pad pad2 = pad.omgekeerd();
            costs.put(key2, pad2);
            return pad;
        }
    }

    public static int getCost(Kaart kaart, Stad stad1, Stad stad2) {
        return getPath(kaart, stad1, stad2).getTotaleTijd();
    }
}
