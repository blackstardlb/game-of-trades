package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.Handelsplan;
import io.gameoftrades.model.markt.actie.Actie;
import io.gameoftrades.model.markt.actie.HandelsPositie;
import io.gameoftrades.student34.notification.NotificationCentre;
import io.gameoftrades.student34.notification.NotificationType;

import java.util.ArrayList;
import java.util.List;

public class HandelsPlanAlgorithmAccurate implements HandelsplanAlgoritme {

    private final int segmentSize = 130;

    @Override
    public Handelsplan bereken(Wereld wereld, HandelsPositie handelsPositie) {
        long start = System.currentTimeMillis();
        List<Actie> totalActies = new ArrayList<>();
        int remainingActies = handelsPositie.getMaxActie();
        int money = handelsPositie.getKapitaal();
        Stad currentStad = handelsPositie.getStad();
        while (remainingActies > 0) {
            HandelsPositie clone = new HandelsPositie(wereld, currentStad, money, handelsPositie.getRuimte(),
                    remainingActies > segmentSize ? segmentSize : remainingActies);
            Tree tree = new Tree(wereld, clone.getStad(), clone.getMaxActie());
            HandelsRoute best = tree.getBest(clone);
            if (best != null) {
                totalActies.addAll(best.getActies());
                remainingActies -= best.getHandelsPositie().getTotaalActie();
                money = best.getHandelsPositie().getKapitaal();
                currentStad = best.getVraagAanboden().get(best.getVraagAanboden().size() - 1).getEindStad();
            } else {
                break;
            }
        }

        if (totalActies.size() == 0) {
            NotificationCentre.showNotification("Er waren geen mogelijke handelsacties gevonden!", NotificationType.ERROR);
        }

        long end = System.currentTimeMillis();
        System.out.println("Took: " + (end - start) + "ms");

        return new Handelsplan(totalActies);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}