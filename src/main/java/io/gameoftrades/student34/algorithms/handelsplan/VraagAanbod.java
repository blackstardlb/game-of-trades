package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.student34.algorithms.stedentour.CostCache;

public class VraagAanbod implements Comparable<VraagAanbod> {

    private final Kaart kaart;
    private final HandelWrapper vraag;
    private final HandelWrapper aanbod;

    public VraagAanbod(Kaart kaart, HandelWrapper vraag, HandelWrapper aanbod) {
        if (vraag.getHandel().getHandelType() == aanbod.getHandel().getHandelType()) {
            throw new IllegalArgumentException("Vraag en aanbod mogen niet hetzelfde handeltype hebben");
        }
        this.kaart = kaart;
        this.vraag = vraag;
        this.aanbod = aanbod;
    }

    private double getCompareWaarde() {
        return vraag.getCompareWaarde() + aanbod.getCompareWaarde();
    }

    private int getWinst() {
        return vraag.getHandel().getPrijs() - aanbod.getHandel().getPrijs();
    }

    private int getTotalTravelCost() {
        return aanbod.getTravelCost() + CostCache.getCost(kaart, vraag.getHandel().getStad(), aanbod.getHandel().getStad());
    }

    @Override
    public int compareTo(VraagAanbod o) {
        return Double.compare(getCompareWaarde(), o.getCompareWaarde());
    }

    @Override
    public String toString() {
        return "VraagAanbod{" +
                "vraag=" + vraag +
                ", aanbod=" + aanbod +
                ", compareWaarde=" + getCompareWaarde() + ", winst=" + getWinst() + ", totalTravelCost=" + getTotalTravelCost() + "}";
    }
}
