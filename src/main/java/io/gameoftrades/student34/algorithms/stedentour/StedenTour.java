package io.gameoftrades.student34.algorithms.stedentour;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Richting;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.student34.PadImpl;
import io.gameoftrades.student34.algorithms.astar.AStarAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StedenTour implements Debuggable {
    private List<Stad> steden;
    private Kaart kaart;
    private Debugger debugger = new DummyDebugger();

    public StedenTour(Kaart kaart, List<Stad> steden, Debugger debugger) {
        this.kaart = kaart;
        this.steden = new ArrayList<>(steden);
        this.debugger = debugger;
    }

    public int getCost() {
        int cost = 0;
        for (int i = 0; i < steden.size() - 1; i++) {
            cost += CostCache.getCost(kaart, steden.get(i), steden.get(i + 1));
        }
        return cost;
    }

    /*
        2optSwap(route, i, k) {
                1. take route[1] to route[i-1] and add them in order to new_route
                2. take route[i] to route[k] and add them in reverse order to new_route
                3. take route[k+1] to end and add them in order to new_route
                return new_route;
            }
    */
    private StedenTour twoOptSwap(int i, int k) {
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
        return new StedenTour(kaart, newRoute, debugger);
    }

    /*
        repeat until no improvement is made {
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
        }
    */
    public StedenTour twoOpt() {
        for (int i = 0; i < steden.size() - 1; i++) {
            for (int k = i + 1; k < steden.size(); k++) {
                StedenTour newRoute = twoOptSwap(i, k);
                this.debugger.debugSteden(kaart, newRoute.getSteden());
                if (newRoute.getCost() < this.getCost()) {
                    return newRoute;
                }
            }
        }
        return this;
    }

    @Override
    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }

    public void deBug() {
        this.debugger.debugSteden(kaart, steden);
    }

    public void deBugPad() {
        AStarAlgorithm aStarAlgorithm = new AStarAlgorithm();
        ArrayList<Richting> richtingen = new ArrayList<>();
        for (int i = 0; i < steden.size() - 1; i++) {
            richtingen.addAll(Arrays.asList(aStarAlgorithm.bereken(kaart, steden.get(i).getCoordinaat(), steden.get(i + 1).getCoordinaat()).getBewegingen()));
        }
        this.debugger.debugPad(kaart, steden.get(0).getCoordinaat(), new PadImpl(richtingen.toArray(new Richting[]{}), getCost()));
    }

    public List<Stad> getSteden() {
        return steden;
    }

    @Override
    public String toString() {
        return "StedenTour{" +
                "cost=" + getCost() +
                ", steden=" + steden +
                '}';
    }
}
