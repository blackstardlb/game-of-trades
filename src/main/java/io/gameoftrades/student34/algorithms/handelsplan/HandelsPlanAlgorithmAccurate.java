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

/**
 * Deze class berekent een accurate handelsplan (kijk naar {@link HandelsplanAlgoritme#bereken(Wereld, HandelsPositie)}
 * voor een meer uitgebreide uitleg over wat een handelsplan is). De snelheid van dit algoritme is vergeleken met ons
 * andere algoritme ({@link HandelsPlanAlgorithmFast}) erg langzaam maar is veel accurater en geeft veel meer winst.
 *
 * @see HandelsplanAlgoritme
 */
public class HandelsPlanAlgorithmAccurate implements HandelsplanAlgoritme {

    /**
     * Bij dit algoritme gebruiken we iets wat erg lijkt op een bruteforce, maar het problem hiermee is, dat hoe hoger
     * het aantal maximale acties wordt, hoe langer het duurt om het te berekenen, en dit groet exponentieel. Om de snelheid
     * van het algoritme te verbeteren, splitsen we de berekening op in kleinere stukken (segments) en dit geeft aan hoe
     * groot deze stukken zijn.
     */
    private final int segmentSize = 130;

    @Override
    public Handelsplan bereken(Wereld wereld, HandelsPositie handelsPositie) {
        long start = System.currentTimeMillis();
        List<Actie> totalActies = new ArrayList<>();
        int remainingActies = handelsPositie.getMaxActie();
        int money = handelsPositie.getKapitaal();
        Stad currentStad = handelsPositie.getStad();
        while (remainingActies > 0) {
            // Zorg dat de huidige berekening atlijd de segmentSize als max aantal acties heeft, behalve als
            // remaningActies kleiner is dan de segmentSize
            HandelsPositie clone = new HandelsPositie(wereld, currentStad, money, handelsPositie.getRuimte(),
                    remainingActies > segmentSize ? segmentSize : remainingActies);
            // Bereken alle mogelijke routes
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
            // Als er geen mogelijke acties zijn gevonden, laat dan een notificatie zien op het scherm
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