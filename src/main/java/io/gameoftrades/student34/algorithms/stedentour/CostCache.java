package io.gameoftrades.student34.algorithms.stedentour;

import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.student34.algorithms.astar.AStarAlgorithm;

import java.util.HashMap;
import java.util.Map;

public class CostCache {

    private static final Map<DoubleMapKey<StadWrapper>, Pad> costs = new HashMap<>();
    private static final SnelstePadAlgoritme snelstePadAlgoritme = new AStarAlgorithm(false);

    public static Pad getPath(Kaart kaart, Stad stad1, Stad stad2) {
        DoubleMapKey<StadWrapper> key = new DoubleMapKey<>(new StadWrapper(stad1), new StadWrapper(stad2));
        DoubleMapKey<StadWrapper> key2 = new DoubleMapKey<>(new StadWrapper(stad2), new StadWrapper(stad1));
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

    public static void clearCache() {
        costs.clear();
    }

    private static class StadWrapper {

        private final Stad stad;

        public StadWrapper(Stad stad) {
            this.stad = stad;
        }

        @Override
        public String toString() {
            return stad.toString();
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof StadWrapper && obj.toString().equalsIgnoreCase(toString());
        }
    }
}
