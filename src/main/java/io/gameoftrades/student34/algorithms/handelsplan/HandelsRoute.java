package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.markt.actie.Actie;
import io.gameoftrades.model.markt.actie.HandelsPositie;
import io.gameoftrades.model.markt.actie.KoopActie;
import io.gameoftrades.model.markt.actie.VerkoopActie;
import io.gameoftrades.student34.utils.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Deze class zal alle acties van meerdere {@link VraagAanbod vraag-aanboden} bij elkaar voegen en berekenen hoeveel
 * winst deze route in totaal oplevert. Dit zal later gebruikt worden om de route te kiezen die het meeste winst oplevert.
 *
 * @see Tree
 * @see VraagAanbod
 * @see HandelsPositie
 */
public class HandelsRoute implements Comparable<HandelsRoute> {

    /**
     * De handelspositie. Deze wordt gebruikt om alle acties op uit te voeren en het houdt bij wat de winst is.
     */
    private HandelsPositie handelsPositie;
    /**
     * Het lijstje met alle vraag-aanboden die je bij deze route uitvoert.
     */
    private final List<VraagAanbod> vraagAanboden;

    public HandelsRoute(List<VraagAanbod> vraagAanboden, HandelsPositie handelsPositie) {
        this.vraagAanboden = vraagAanboden;
        this.handelsPositie = cloneHandelsPositie(handelsPositie);
        // Loop door alle vraag-aanboden heen en voer alle acties uit, hierna hebben we een HandelsPositie object
        // waar alle acties van deze route op zijn uitgevoerd, dit object heeft dan ook het aantal winst dat je hebt gemaakt
        // met deze specifieke route.
        vraagAanboden.forEach(vraagAanbod -> vraagAanbod.getActies().forEach(actie -> {
            if (actie.isMogelijk(HandelsRoute.this.handelsPositie) || (actie instanceof KoopActie || actie instanceof VerkoopActie)) {
                HandelsRoute.this.handelsPositie = actie.voerUit(HandelsRoute.this.handelsPositie);
            }
        }));
    }

    /**
     * @return Geeft de handelspositie terug
     */
    public HandelsPositie getHandelsPositie() {
        return handelsPositie;
    }

    /**
     * @return Geeft alle acties die je moet uitvoeren om deze route te volgen
     */
    public List<Actie> getActies() {
        List<Actie> acties = new ArrayList<>();
        vraagAanboden.forEach(vraagAanbod -> acties.addAll(vraagAanbod.getActies()));
        return acties;
    }

    public int getTotaalTravelCost() {
        int total = 0;
        for (VraagAanbod vraagAanbod : vraagAanboden) {
            total += vraagAanbod.getTotalTravelCost();
        }
        return total;
    }

    public int getTotalWinst() {
        return handelsPositie.getTotaalWinst();
    }

    public List<VraagAanbod> getVraagAanboden() {
        return vraagAanboden;
    }

    @Override
    public int compareTo(HandelsRoute o) {
        int compare = Integer.compare(o.getTotalWinst(), getTotalWinst());
        return compare == 0 ? Integer.compare(o.getTotaalTravelCost(), getTotaalTravelCost()) : compare;
    }

    /**
     * Cloned een instance van {@link HandelsPositie}. We maken gebruikt van reflection om een constructor aan te roepen
     * waar we normaal niet bijkunnen, dit maakt het voor ons makkelijker om het {@link HandelsPositie} object te clonen
     *
     * @param handelsPositie De handelspositie die ge-cloned wordt.
     * @return Het gecloonde object.
     * @see HandelsPositie#HandelsPositie(HandelsPositie)
     * @see HandelsPositie
     */
    private HandelsPositie cloneHandelsPositie(HandelsPositie handelsPositie) {
        return ReflectionUtil.newInstance(HandelsPositie.class, new Class<?>[]{HandelsPositie.class}, new Object[]{handelsPositie});
    }
}
