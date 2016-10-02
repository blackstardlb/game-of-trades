package io.gameoftrades.student34.algorithms.stedentour;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Richting;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.student34.PadImpl;
import io.gameoftrades.student34.algorithms.astar.AStarAlgorithm;

import java.util.*;

public class StedenTourAlgorithm implements StedenTourAlgoritme, Debuggable {
    private Debugger debugger = new DummyDebugger();

    @Override
    public List<Stad> bereken(Kaart kaart, List<Stad> steden) {
        return optimiseSeveralTimes(kaart, steden).getSteden();
    }

    private StedenTour optimiseSeveralTimes(Kaart kaart, List<Stad> steden) {
        List<Stad> randomList = new ArrayList<>();
        randomList.addAll(steden);

        StedenTour best = null;
        StedenTour worst = null;

        for (int i = 0; i < steden.size(); i++) {
            Collections.shuffle(randomList);
            StedenTour optimisedStedenTour = this.optimiseStadList(kaart, randomList);
            if (best == null) {
                best = optimisedStedenTour;
                worst = optimisedStedenTour;
            }

            if (optimisedStedenTour.getCost() < best.getCost()) {
                best = optimisedStedenTour;
            } else if (optimisedStedenTour.getCost() > worst.getCost()) {
                worst = optimisedStedenTour;
            }
        }

        if (best != null) {
            System.out.println("Min Time: " + best.getCost());
            System.out.println("Max Time: " + worst.getCost());
            best.setDebugger(debugger);
            best.deBug();
        }
        return best;
    }

    private StedenTour optimiseStadList(Kaart kaart, List<Stad> steden) {
        StedenTour stedenTour = new StedenTour(kaart, steden, new DummyDebugger());
        StedenTour stedenTour2;

        while (stedenTour.getCost() > (stedenTour2 = stedenTour.twoOpt()).getCost()) {
            stedenTour = stedenTour2;
        }

        stedenTour.setDebugger(debugger);
        stedenTour.deBug();
        return stedenTour;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }
}
