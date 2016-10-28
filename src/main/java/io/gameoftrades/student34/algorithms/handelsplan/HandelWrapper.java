package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.HandelType;
import io.gameoftrades.student34.algorithms.stedentour.CostCache;

/**
 * Dit is een wrapper om de {@link Handel} class die we hebben gemaakt om {@link Handel} met elkaar te vergelijken.
 *
 * @see Handel
 */
public class HandelWrapper implements Comparable<HandelWrapper> {

    /**
     * De handel die ge-wrapped wordt
     */
    private final Handel handel;
    /**
     * De beginstad. Deze wordt gebruikt om te berekenen hoeveel actie deze handel precies kost.
     */
    private final Stad beginStad;
    /**
     * Het totaal aantal acties dat het kost om deze handel uit te voeren. Dit wordt berekent in {@link #HandelWrapper(Kaart, Handel, Stad)}
     */
    private final int travelCost;
    /**
     * Dit is een waarde die we berekenen die we gebruiken bij het vergelijken van twee Handels.
     */
    private final double compareWaarde;

    /**
     * In deze constructor wordt berekend hoeveel stappen het kost om van de beginstad naar de stad waar de handel is te komen.
     * Het berekent ook een {@link #compareWaarde} die later gebruikt wordt om twee {@link Handel} objecten te vergelijken.
     *
     * @param kaart     De kaart waarop de handel staat
     * @param handel    De handel
     * @param beginStad De beginstad
     */
    public HandelWrapper(Kaart kaart, Handel handel, Stad beginStad) {
        this.handel = handel;
        this.beginStad = beginStad;
        this.travelCost = CostCache.getCost(kaart, handel.getStad(), beginStad);
        this.compareWaarde = handel.getHandelType() == HandelType.BIEDT ? handel.getPrijs() * Math.max(travelCost, 0.1) : 1 / (handel.getPrijs() / Math.max(travelCost, 0.1));
    }

    /**
     * @return Geeft de handel terug
     * @see Handel
     */
    public Handel getHandel() {
        return handel;
    }

    /**
     * @return Geeft de beginstad terug
     * @see Stad
     */
    public Stad getBeginStad() {
        return beginStad;
    }

    /**
     * @return Geeft het totaal aantal stappen dat het kost om van de beginstad naar de handel te komen, en de handel uit te voeren
     */
    public int getTravelCost() {
        return travelCost;
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
