package io.gameoftrades.student34.algorithms.stedentour;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;

import java.util.ArrayList;
import java.util.List;

public class StedenTour implements Debuggable {

    private List<Stad> steden;
    private Kaart kaart;
    private Debugger debugger = new DummyDebugger();


    /**
     * @param kaart    De kaart waarbij deze StedenTour behoort.
     * @param steden   De lijst van steden.
     * @param debugger De debugger die gebruikt zal worden voor het debuggen van deze StedenTour.
     */
    public StedenTour(Kaart kaart, List<Stad> steden, Debugger debugger) {
        this.kaart = kaart;
        this.steden = new ArrayList<>(steden);
        this.debugger = debugger;
    }

    /**
     * @return De cost van de StedenTour.
     */
    public int getCost() {
        int cost = 0;
        for (int i = 0; i < steden.size() - 1; i++) {
            cost += CostCache.getCost(kaart, steden.get(i), steden.get(i + 1));
        }
        return cost;
    }

    /**
     * Voeg stad 0 tot i aan de lijst.
     * Voeg dan stad i tot en met k omgekeerd aan de lijst.
     * Voeg als laatst de rest van de steden aan de lijst
     *
     * @param i de begin stad voor te swappen
     * @param k de eind stad voor het swappen
     * @return De nieuwe StedenTour.
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

    /**
     * @return De de better / zelfde StedenTour die gevonden is na gebruik te maken van de Two-Opt algorithme.
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

    /**
     * @param debugger Set de debugger.
     */
    @Override
    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }

    /**
     * Deze methode wordt gebruikt om de stedentour route visuele te debuggen.
     */
    public void deBug() {
        this.debugger.debugSteden(kaart, steden);
    }

    /**
     * @return De lijst van steden voor deze stedentour.
     */
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
