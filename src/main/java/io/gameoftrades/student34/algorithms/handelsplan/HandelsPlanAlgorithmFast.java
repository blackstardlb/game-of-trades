package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.Handelsplan;
import io.gameoftrades.model.markt.actie.Actie;
import io.gameoftrades.model.markt.actie.HandelsPositie;
import io.gameoftrades.student34.NullPad;
import io.gameoftrades.student34.algorithms.stedentour.CostCache;
import io.gameoftrades.student34.notification.NotificationCentre;
import io.gameoftrades.student34.notification.NotificationType;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * Deze class berekent het handelsplan op een snelle manier, vergeleken met {@link HandelsPlanAlgorithmAccurate}. Het
 * nadeel van dit algoritme is dat de winst maar zelden optimaal is.
 */
public class HandelsPlanAlgorithmFast implements HandelsplanAlgoritme {

    /**
     * Het aantal acties die nog uitgevoerd mogen worden
     */
    private int stepsLeft;
    /**
     * Het maximale aantal goederen die je tegelijk kunt dragen
     *
     * @see HandelsPositie#getRuimte()
     */
    private int voorraad;
    /**
     * Het aantal geld dat je nog over hebt.
     */
    private int geld;

    @Override
    public Handelsplan bereken(Wereld wereld, HandelsPositie positie) {
        stepsLeft = positie.getMaxActie();
        voorraad = positie.getRuimte();
        geld = positie.getKapitaal();

        long start = System.currentTimeMillis();
        List<Actie> acties = berekenPlan(wereld, positie.getStad());
        Handelsplan handelsplan = new Handelsplan(acties);
        long end = System.currentTimeMillis();
        System.out.println("Took: " + (end - start) + "ms");

        if (acties.size() == 0) {
            // Als er geen mogelijke acties zijn gevonden, laat dan een notificatie zien op het scherm
            NotificationCentre.showNotification("Er waren geen mogelijke handelsacties gevonden!", NotificationType.ERROR);
        }

        return handelsplan;
    }

    /**
     * Bereken het 'beste' handelsplan op een efficient manier. Hij zal altijd de route nemen waarvan het winst-aantal stappen
     * ratio het beste is.
     *
     * @param wereld    De wereld waaring het handelsplan moet worden berekend
     * @param beginStad De stad waar je begint
     * @return Geef de acties terug die je moet nemen voor het berekende handelsplan
     * @see Actie
     * @see #getVraagEnAanbod(Wereld, Stad)
     */
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
        return acties;
    }


    /**
     * Bereken een lijst met {@link VraagAanbod vraag-aanboden} die je kunt nemen vanaf een bepaalde stad. Dit geeft alleen de
     * {@link VraagAanbod vraag-aanboden} acties die nog modelijk zijn, dus het kijkt naar het aantal acties die je nog mag uitvoeren.
     *
     * @param wereld    De wereld waaring het handelsplan moet worden berekend
     * @param beginStad De stad waarvan je de mogelijke vraag-aanboden wil bereken
     * @return Een lijst met mogelijke vraag-aanboden
     * @see VraagAanbod
     * @see HandelWrapper
     */
    private PriorityQueue<VraagAanbod> getVraagEnAanbod(Wereld wereld, Stad beginStad) {
        PriorityQueue<HandelWrapper> alleAanbod = new PriorityQueue<>(
                wereld.getMarkt().getAanbod().stream().map(handel -> new HandelWrapper(wereld.getKaart(), handel, beginStad))
                        .collect(Collectors.toList()));
        PriorityQueue<VraagAanbod> vraagAanboden = new PriorityQueue<>();
        for (HandelWrapper aanbodWrapper : alleAanbod) {
            for (Handel vraag : wereld.getMarkt().getVraag()) {
                if (vraag.getHandelswaar().equals(aanbodWrapper.getHandel().getHandelswaar())) {
                    if (!(CostCache.getPath(wereld.getKaart(), beginStad, aanbodWrapper.getHandel().getStad()) instanceof NullPad)
                            && !(CostCache.getPath(wereld.getKaart(), aanbodWrapper.getHandel().getStad(), vraag.getStad()) instanceof NullPad)) {
                        VraagAanbod vraagAanbod = new VraagAanbod(wereld.getKaart(),
                                new HandelWrapper(wereld.getKaart(), vraag, aanbodWrapper.getHandel().getStad()), aanbodWrapper, voorraad, geld);
                        // Kijk of het vraagaanbod wel mogelijk is, dus dat hij niet meer kost dan het aantal acties die nog mag uitvoeren
                        if (stepsLeft >= vraagAanbod.getTotalTravelCost()) {
                            vraagAanboden.add(vraagAanbod);
                        }
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
