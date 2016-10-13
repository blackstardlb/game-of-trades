package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.markt.actie.Actie;
import io.gameoftrades.model.markt.actie.HandelsPositie;
import io.gameoftrades.model.markt.actie.KoopActie;
import io.gameoftrades.model.markt.actie.VerkoopActie;
import io.gameoftrades.student34.utils.ReflectionUtil;

import java.util.ArrayList;
import java.util.List;

public class HandelsRoute implements Comparable<HandelsRoute> {

    private HandelsPositie handelsPositie;
    private final List<VraagAanbod> vraagAanboden;

    public HandelsRoute(List<VraagAanbod> vraagAanboden, HandelsPositie handelsPositie) {
        this.vraagAanboden = vraagAanboden;
        this.handelsPositie = cloneHandelsPositie(handelsPositie);
        vraagAanboden.forEach(vraagAanbod -> vraagAanbod.getActies().forEach(actie -> {
            if (actie.isMogelijk(HandelsRoute.this.handelsPositie) || (actie instanceof KoopActie || actie instanceof VerkoopActie)) {
                HandelsRoute.this.handelsPositie = actie.voerUit(HandelsRoute.this.handelsPositie);
            }
        }));
    }

    public HandelsPositie getHandelsPositie() {
        return handelsPositie;
    }

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

    private HandelsPositie cloneHandelsPositie(HandelsPositie handelsPositie) {
        return ReflectionUtil.newInstance(HandelsPositie.class, new Class<?>[]{HandelsPositie.class}, new Object[]{handelsPositie});
    }
}
