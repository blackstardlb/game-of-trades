package io.gameoftrades.student34.algorithms.stedentour;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Richting;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.student34.MyPad;
import io.gameoftrades.student34.algorithms.astar.AStarAlgorithm;

import java.util.*;

public class StedenTourAlgorithm implements StedenTourAlgoritme, Debuggable {
    private AStarAlgorithm aStarAlgorithm = new AStarAlgorithm();
    private Debugger debugger = new DummyDebugger();
    private Map<DoubleMapKey<Stad>, Integer> costs = new HashMap<>();

    @Override
    public List<Stad> bereken(Kaart kaart, List<Stad> steden) {
        return optimiseSeveralTimes(kaart, steden);
    }

    private List<Stad> optimiseSeveralTimes(Kaart kaart, List<Stad> steden) {
        List<Stad> randomList = new ArrayList<>();
        randomList.addAll(steden);

        List<Stad> best = new ArrayList<>();
        best.addAll(steden);

        List<Stad> worst = new ArrayList<>();
        worst.addAll(steden);

        for (int i = 0; i < 1000; i++) {
            List<Stad> optimiseStadList = this.optimiseStadList(kaart, randomList);
//            this.debugger.debugSteden(kaart, optimiseStadList);
            if (getCost(kaart, optimiseStadList) < getCost(kaart, best)) {
                best = optimiseStadList;
            } else if (getCost(kaart, optimiseStadList) > getCost(kaart, worst)) {
                worst = optimiseStadList;
            }

            Collections.shuffle(randomList, new Random(System.currentTimeMillis()));
        }

        System.out.println("Min Time: " + getCost(kaart, best));
        System.out.println("Max Time: " + getCost(kaart, worst));
        this.debugger.debugSteden(kaart, best);
//        this.debugger.debugPad(kaart, best.get(0).getCoordinaat(), this.createPad(kaart, best));
        return best;
    }

    private List<Stad> optimiseStadList(Kaart kaart, List<Stad> steden) {
        List<Stad> toReturn = new ArrayList<>();
        toReturn.addAll(steden);
        while (true) {
            List<Stad> temp = twoOpt(kaart, toReturn);
            if (temp.equals(toReturn)) {
                break;
            } else {
                toReturn = temp;
            }
        }
        return toReturn;
    }

    /*repeat until no improvement is made {
       start_again:
       best_distance = calculateTotalDistance(existing_route)
       for (i = 0; i < number of nodes eligible to be swapped - 1; i++) {
           for (k = i + 1; k < number of nodes eligible to be swapped; k++) {
               new_route = 2optSwap(existing_route, i, k)
               new_distance = calculateTotalDistance(new_route)
               if (new_distance < best_distance) {
                   existing_route = new_route
                  goto start_again
               }
           }
       }
       }*/
    private List<Stad> twoOpt(Kaart kaart, List<Stad> steden) {
        int bestDistance = getCost(kaart, steden);
        for (int i = 0; i < steden.size() - 1; i++) {
            for (int k = i + 1; k < steden.size(); k++) {
                List<Stad> newRoute = twoOptSwap(steden, i, k);
                int newDistance = getCost(kaart, newRoute);
                if (newDistance < bestDistance) {
//                    this.debugger.debugSteden(kaart, newRoute);
                    return newRoute;
                }
            }
        }
        return steden;
    }

    /*2optSwap(route, i, k) {
            1. take route[1] to route[i-1] and add them in order to new_route
            2. take route[i] to route[k] and add them in reverse order to new_route
            3. take route[k+1] to end and add them in order to new_route
            return new_route;
        }*/
    private List<Stad> twoOptSwap(List<Stad> steden, int i, int k) {
        ArrayList<Stad> newRoute = new ArrayList<>();
        for (int y = 0; y < i; y++) {
            newRoute.add(steden.get(y));
        }

        for (int j = k; j >= i; j--) {
            newRoute.add(steden.get(j));
        }

        for (int z = k + 1; z < steden.size(); z++) {
            newRoute.add(steden.get(z));
        }

//        System.out.println("New Route Size: " + newRoute.size());
        return newRoute;
    }

    private Pad createPad(Kaart kaart, List<Stad> steden) {
        ArrayList<Richting> richtingen = new ArrayList<>();
        for (int i = 0; i < steden.size() - 1; i++) {
            richtingen.addAll(Arrays.asList(aStarAlgorithm.bereken(kaart, steden.get(i).getCoordinaat(), steden.get(i + 1).getCoordinaat()).getBewegingen()));
        }
        return new MyPad(richtingen.toArray(new Richting[]{}), getCost(kaart, steden));
    }

    private int getCost(Kaart kaart, List<Stad> steden) {
        int cost = 0;
        for (int i = 0; i < steden.size() - 1; i++) {
            cost += this.getCost(kaart, steden.get(i), steden.get(i + 1));
        }
        return cost;
    }

    private int getCost(Kaart kaart, Stad stad1, Stad stad2) {
        DoubleMapKey<Stad> key = new DoubleMapKey<>(stad1, stad2);
        if (costs.containsKey(key)) {
            return costs.get(key);
        } else {
            int cost = aStarAlgorithm.bereken(kaart, stad1.getCoordinaat(), stad2.getCoordinaat()).getTotaleTijd();
            costs.put(key, cost);
            return cost;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }

    private class DoubleMapKey<T> {

        private final T x;
        private final T y;

        public DoubleMapKey(T x, T y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof DoubleMapKey) {
                DoubleMapKey key = (DoubleMapKey) o;
                return (this.x.equals(key.x) && this.y.equals(key.y)) || (this.y.equals(key.x) && this.x.equals(key.y));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = result * 37 + Math.max(x.hashCode(), y.hashCode());
            result = result * 37 + Math.min(x.hashCode(), y.hashCode());
            return result;
        }

    }
}
