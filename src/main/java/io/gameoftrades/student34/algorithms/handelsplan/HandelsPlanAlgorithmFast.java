package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.Handelsplan;
import io.gameoftrades.model.markt.actie.Actie;
import io.gameoftrades.model.markt.actie.HandelsPositie;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class HandelsPlanAlgorithmFast implements HandelsplanAlgoritme {

    private int stepsLeft;
    private int voorraad;
    private int geld;

    @Override
    public Handelsplan bereken(Wereld wereld, HandelsPositie positie) {
        stepsLeft = positie.getMaxActie();
        voorraad = positie.getRuimte();
        geld = positie.getKapitaal();

        long start = System.currentTimeMillis();
        Handelsplan handelsplan = new Handelsplan(berekenPlan(wereld, positie.getStad()));
        long end = System.currentTimeMillis();
        System.out.println("Took: " + (end - start) + "ms");

        return handelsplan;
    }

    private List<Actie> berekenPlan(Wereld wereld, Stad beginStad) {
        List<Actie> acties = new ArrayList<>();
        PriorityQueue<VraagAanbod> vraagAanboden = getVraagEnAanbod(wereld, beginStad);
        if (vraagAanboden.size() > 0) {
            VraagAanbod beste = vraagAanboden.poll();
            stepsLeft -= beste.getTotalTravelCost();
            geld = beste.getGeldAfter();
            acties.addAll(beste.getActies());
            acties.addAll(berekenPlan(wereld, beste.getEindStad()));
        }
        System.out.println(geld);
        return acties;
    }


    private PriorityQueue<VraagAanbod> getVraagEnAanbod(Wereld wereld, Stad beginStad) {
        PriorityQueue<HandelWrapper> alleAanbod = new PriorityQueue<>(wereld.getMarkt().getAanbod().stream().map(handel -> new HandelWrapper(wereld.getKaart(), handel, beginStad)).collect(Collectors.toList()));
        PriorityQueue<VraagAanbod> vraagAanboden = new PriorityQueue<>();
        for (HandelWrapper aanbodWrapper : alleAanbod) {
            for (Handel vraag : wereld.getMarkt().getVraag()) {
                if (vraag.getHandelswaar().equals(aanbodWrapper.getHandel().getHandelswaar())) {
                    VraagAanbod vraagAanbod = new VraagAanbod(wereld.getKaart(), new HandelWrapper(wereld.getKaart(), vraag, aanbodWrapper.getHandel().getStad()), aanbodWrapper/*, voorraad, geld*/);
                    if (stepsLeft >= vraagAanbod.getTotalTravelCost()) {
                        vraagAanboden.add(vraagAanbod);
                    }
                }
            }
        }
        return vraagAanboden;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
