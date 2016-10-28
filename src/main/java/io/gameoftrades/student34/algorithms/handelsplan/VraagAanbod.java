package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.actie.*;
import io.gameoftrades.student34.algorithms.stedentour.CostCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Deze class houdt informatie bij over het kopen en verkopen op bepaalde plekken. Het berekent hoeveel stappen het kost
 * om van de beginstad naar een bepaalde stad te gaan waar je iets inkoopt, en daarna naar een andere stad te gaan om het
 * daar te kopen. Dit wordt gebruikt om te vergelijken welke koop- en verkoop acties het beste zijn, en hoogstwaarschijnlijk
 * het meeste winst zullen geven.
 *
 * @see HandelWrapper
 */
public class VraagAanbod implements Comparable<VraagAanbod> {

    /**
     * De kaart waar het vraag-aanbod zich op begeeft.
     */
    private final Kaart kaart;
    /**
     * Het maximaal aantal goederen dat je nog kunt dragen
     */
    private int voorraad;
    /**
     * Het geld dat je hebt.
     */
    private int geld;
    /**
     * De handel waar je je goederen verkoopt.
     *
     * @see HandelWrapper
     */
    private final HandelWrapper vraag;
    /**
     * De handel waar je je goederen inkoopt.
     *
     * @see HandelWrapper
     */
    private final HandelWrapper aanbod;

    public VraagAanbod(Kaart kaart, HandelWrapper vraag, HandelWrapper aanbod) {
        this(kaart, vraag, aanbod, 0, 0);
    }

    public VraagAanbod(Kaart kaart, HandelWrapper vraag, HandelWrapper aanbod, int voorraad, int geld) {
        if (vraag.getHandel().getHandelType() == aanbod.getHandel().getHandelType()) {
            throw new IllegalArgumentException("Vraag en aanbod mogen niet hetzelfde handeltype hebben");
        }
        this.kaart = kaart;
        this.vraag = vraag;
        this.aanbod = aanbod;
        this.voorraad = voorraad;
        this.geld = geld;
    }

    /**
     * @return Geeft de kaart terug
     */
    public Kaart getKaart() {
        return kaart;
    }

    /**
     * @return Berekent een waarde die later gebruikt wordt om twee vraag-aanboden met elkaar te vergelijken.
     */
    private double getCompareWaarde() {
        return ((double) getWinst() * (Math.max(1, voorraad)) / ((double) getTotalTravelCost()));
    }

    /**
     * @return Geeft het aantal winst dat deze vraag-aanbod oplevert.
     */
    private int getWinst() {
        int aantal = voorraad > 0 ? Math.min(voorraad, geld / aanbod.getHandel().getPrijs()) : 1;
        return (vraag.getHandel().getPrijs() * aantal) - (aanbod.getHandel().getPrijs() * aantal);
    }

    /**
     * @return Berekent hoeveel geld je hebt nadat je deze vraag-aanbod uit zou voeren.
     */
    public int getGeldAfter() {
        return geld + getWinst();
    }

    /**
     * @return Geeft het aantal stappen die je moet nemen om deze vraag-aanbod uit te voeren
     */
    public int getTotalTravelCost() {
        return aanbod.getTravelCost() + vraag.getTravelCost() + 2;
    }

    @Override
    public int compareTo(VraagAanbod o) {
        return Double.compare(o.getCompareWaarde(), getCompareWaarde());
    }

    /**
     * @return Geeft de stad waar je eindigt als je deze vraag-aanbod volgt. Dit is de stad waar je je goederen verkoopt.
     */
    public Stad getEindStad() {
        return vraag.getHandel().getStad();
    }

    @Override
    public String toString() {
        return "VraagAanbod{" +
                "verkoopt=" + aanbod.getHandel().getHandelswaar() +
                ", beginstad=" + aanbod.getBeginStad().getNaam() +
                ", kooptstad=" + aanbod.getHandel().getStad().getNaam() +
                ", eindstad=" + getEindStad().getNaam() +
                ", totalTravelCost=" + getTotalTravelCost() +
                "}";
    }

    /**
     * Geeft alle acties die je moet nemen om van een bepaalde beginstad naar een bepaalde andere stad te gaan om goederen
     * in te kopen en daarna ergens anders te verkopen. Deze acties kunnen later uitgevoerd worden op een {@link HandelsPositie}
     *
     * @return Geeft alle acties terug die je moet nemen voor deze vraag-aanbod.
     * @see Actie
     */
    public List<Actie> getActies() {
        ArrayList<Actie> acties = new ArrayList<>();
        BeweegActie beweegActie = new BeweegActie(kaart, aanbod.getBeginStad(), aanbod.getHandel().getStad(), CostCache.getPath(kaart, aanbod.getBeginStad(), aanbod.getHandel().getStad()));
        acties.addAll(beweegActie.naarNavigatieActies());
        acties.add(new KoopActie(aanbod.getHandel()));
        BeweegActie beweegActie2 = new BeweegActie(kaart, vraag.getBeginStad(), vraag.getHandel().getStad(), CostCache.getPath(kaart, vraag.getBeginStad(), vraag.getHandel().getStad()));
        acties.addAll(beweegActie2.naarNavigatieActies());
        acties.add(new VerkoopActie(vraag.getHandel()));
        return acties;
    }
}