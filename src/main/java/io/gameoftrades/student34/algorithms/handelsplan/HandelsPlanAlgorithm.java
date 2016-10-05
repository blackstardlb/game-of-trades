package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.Handelsplan;
import io.gameoftrades.model.markt.actie.BeweegActie;
import io.gameoftrades.model.markt.actie.HandelsPositie;
import io.gameoftrades.model.markt.actie.NavigeerActie;

import java.util.*;
import java.util.stream.Collectors;

public class HandelsPlanAlgorithm implements HandelsplanAlgoritme {

    @Override
    public Handelsplan bereken(Wereld wereld, HandelsPositie handelsPositie) {
        Map<Stad, List<HandelWrapper>> aanbod = new HashMap<>();
        Map<Stad, List<HandelWrapper>> vraag = new HashMap<>();
        PriorityQueue<HandelWrapper> alleAanbod = new PriorityQueue<>(wereld.getMarkt().getAanbod().stream().map(handel -> new HandelWrapper(wereld.getKaart(), handel, handelsPositie.getStad())).collect(Collectors.toList()));
        PriorityQueue<HandelWrapper> alleVraag = new PriorityQueue<>(wereld.getMarkt().getVraag().stream().map(handel -> new HandelWrapper(wereld.getKaart(), handel, handelsPositie.getStad())).collect(Collectors.toList()));
        PriorityQueue<VraagAanbod> vraagAanboden = new PriorityQueue<>();

        wereld.getMarkt().getAanbod().stream().forEach(handel -> {
            if (!aanbod.containsKey(handel.getStad())) {
                aanbod.put(handel.getStad(), new ArrayList<>());
            }
            aanbod.get(handel.getStad()).add(new HandelWrapper(wereld.getKaart(), handel, handelsPositie.getStad()));
        });
        wereld.getMarkt().getVraag().stream().forEach(handel -> {
            if (!vraag.containsKey(handel.getStad())) {
                vraag.put(handel.getStad(), new ArrayList<>());
            }
            vraag.get(handel.getStad()).add(new HandelWrapper(wereld.getKaart(), handel, handelsPositie.getStad()));
        });

        for (HandelWrapper vraagWrapper : alleVraag) {
            for (HandelWrapper aanbodWrapper : alleAanbod) {
                if (vraagWrapper.getHandel().getHandelswaar().equals(aanbodWrapper.getHandel().getHandelswaar())) {
                    vraagAanboden.add(new VraagAanbod(wereld.getKaart(), vraagWrapper, aanbodWrapper));
                }
            }
        }

        while (!alleAanbod.isEmpty()) {
            System.out.println(alleAanbod.poll());
        }
        System.out.println("");
        while (!alleVraag.isEmpty()) {
            System.out.println(alleVraag.poll());
        }

        System.out.println("");
        while (!vraagAanboden.isEmpty()) {
            System.out.println(vraagAanboden.poll());
        }
        return new Handelsplan(new ArrayList<>());
    }
}
