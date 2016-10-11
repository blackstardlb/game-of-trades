package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.actie.*;
import io.gameoftrades.student34.algorithms.stedentour.CostCache;

import java.util.ArrayList;
import java.util.List;

public class VraagAanbod implements Comparable<VraagAanbod> {

    private final Kaart kaart;
    private int voorraad;
    private int geld;
    private final HandelWrapper vraag;
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

    public Kaart getKaart() {
        return kaart;
    }

    private double getCompareWaarde() {
        return ((double) getWinst() * (Math.max(1, voorraad)) / ((double) getTotalTravelCost()));
    }

    private int getWinst() {
        int aantal = voorraad > 0 ? Math.min(voorraad, geld / aanbod.getHandel().getPrijs()) : 1;
        return (vraag.getHandel().getPrijs() * aantal) - (aanbod.getHandel().getPrijs() * aantal);
    }

    public int getGeldAfter() {
        return geld + getWinst();
    }

    public int getTotalTravelCost() {
        return aanbod.getTravelCost() + vraag.getTravelCost() + 2;
    }

    @Override
    public int compareTo(VraagAanbod o) {
        return Double.compare(o.getCompareWaarde(), getCompareWaarde());
    }

    public HandelWrapper getAanbod() {
        return aanbod;
    }

    public HandelWrapper getVraag() {
        return vraag;
    }

    public Stad getBeginStad() {
        return aanbod.getBeginStad();
    }

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