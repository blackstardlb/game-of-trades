package io.gameoftrades.student34.algorithms.stedentour;

import io.gameoftrades.debug.Debuggable;
import io.gameoftrades.debug.Debugger;
import io.gameoftrades.debug.DummyDebugger;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StedenTourAlgorithm implements StedenTourAlgoritme, Debuggable {
    private Debugger debugger = new DummyDebugger();

    /**
     * @param kaart  De kaart de gebruikt zal worden.
     * @param steden De te sorteren lijst van steden.
     * @return De optimaal gesoorterd lijst van steden.
     */
    @Override
    public List<Stad> bereken(Kaart kaart, List<Stad> steden) {
        return optimiseSeveralTimes(kaart, steden).getSteden();
    }

    /**
     * Deze methode gebruikt de optimiseStadList() methode meerdere keeren returned het beste StedenTour
     *
     * @param kaart  De kaart de gebruikt zal worden.
     * @param steden De te sorteren lijst van steden.
     * @return De optimaal gesoorterd StedenTour.
     */
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

    /**
     * @param kaart  De kaart de gebruikt zal worden.
     * @param steden De te sorteren lijst van steden.
     * @return De optimaal gesoorterd StedenTour.
     */
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

    /**
     * @param debugger Set de debugger.
     */
    @Override
    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }
}
