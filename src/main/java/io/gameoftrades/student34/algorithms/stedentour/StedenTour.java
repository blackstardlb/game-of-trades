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
