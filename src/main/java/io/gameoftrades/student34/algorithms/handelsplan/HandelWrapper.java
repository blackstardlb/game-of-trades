package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.HandelType;
import io.gameoftrades.student34.algorithms.stedentour.CostCache;

public class HandelWrapper implements Comparable<HandelWrapper> {

    private final Handel handel;
    private final Stad beginStad;
    private final int travelCost;
    private final double compareWaarde;

    public HandelWrapper(Kaart kaart, Handel handel, Stad beginStad) {
        this.handel = handel;
        this.beginStad = beginStad;
        this.travelCost = CostCache.getCost(kaart, handel.getStad(), beginStad);
        this.compareWaarde = handel.getHandelType() == HandelType.BIEDT ? handel.getPrijs() * Math.max(travelCost, 0.1) : 1 / (handel.getPrijs() / Math.max(travelCost, 0.1));
    }

    public Handel getHandel() {
        return handel;
    }

    public Stad getBeginStad() {
        return beginStad;
    }

    public int getTravelCost() {
        return travelCost;
    }

    public double getCompareWaarde() {
        return compareWaarde;
    }

    @Override
    public int compareTo(HandelWrapper o) {
        if (handel.getHandelType() != o.handel.getHandelType()) {
            throw new IllegalArgumentException("Handel type moet hetzelfde zijn");
        }
        return Double.compare(compareWaarde, o.compareWaarde);
    }

    @Override
    public String toString() {
        return "HandelWrapper{" +
                "handel=" + handel +
                ", beginStad=" + beginStad +
                ", travelCost=" + travelCost +
                ", compareWaarde=" + (compareWaarde) + "}";
    }
}
