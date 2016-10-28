package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.actie.HandelsPositie;
import io.gameoftrades.student34.NullPad;
import io.gameoftrades.student34.algorithms.stedentour.CostCache;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * Deze class berekent alle mogelijke {@link HandelsRoute handelsroutes} die je kunt nemen vanaf een bepaalde beginstad.
 *
 * @see HandelsRoute
 * @see HandelsPlanAlgorithmAccurate
 */
public class Tree {

    private Stad currentStad;
    private VraagAanbod currentVraagAanbod;
    private Tree parent;
    private List<Tree> children;
    private int actionsLeft;

    public Tree(Wereld wereld, Stad currentStad, int actionsLeft) {
        this(wereld, currentStad, actionsLeft, null, null);
    }

    private Tree(Wereld wereld, Stad currentStad, int actionsLeft, Tree parent, VraagAanbod currentVraagAanbod) {
        this.currentStad = currentStad;
        this.actionsLeft = actionsLeft;
        this.currentVraagAanbod = currentVraagAanbod;
        this.parent = parent;
        this.children = getVraagEnAanbod(wereld, currentStad)
                .stream()
                .map(vraagAanbod -> new Tree(wereld, vraagAanbod.getEindStad(), actionsLeft - vraagAanbod.getTotalTravelCost(), this, vraagAanbod))
                .collect(Collectors.toList());
    }

    /**
     * @return Geeft de huidige stad terug
     */
    public Stad getCurrentStad() {
        return currentStad;
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
                                new HandelWrapper(wereld.getKaart(), vraag, aanbodWrapper.getHandel().getStad()), aanbodWrapper);
                        if (actionsLeft - vraagAanbod.getTotalTravelCost() >= 0) {
                            vraagAanboden.add(vraagAanbod);
                        }
                    }
                }
            }
        }
        return vraagAanboden;
    }

    /**
     * @return Kijkt of de huidige tree node children heeft, dus of het een leaf is of niet.
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * Geeft het best mogelijke handelsroute die er berekent is.
     *
     * @param handelsPositie De begin handelspositie, deze wordt gebruikt om te berekenen welke handelsroute het beste is.
     * @return Geeft het best mogelijke handelsroute die er berekent is.
     * @see HandelsRoute
     */
    public HandelsRoute getBest(HandelsPositie handelsPositie) {
        PriorityQueue<HandelsRoute> handelsRoutes = new PriorityQueue<>(getAllLeaves(handelsPositie));
        return handelsRoutes.poll();
    }

    /**
     * Berekent de {@link HandelsRoute} vanaf een bepaald punt in de Tree. Het vraagt eerst alle {@link VraagAanbod vraag-aanboden}
     * van zijn parent op (recursie) en zal deze samenvoegen tot een {@link HandelsRoute}
     *
     * @param handelsPositie De begin handelspositie.
     * @return Geeft de handelsroute vanaf een bepaald punt in de Tree.
     */
    private HandelsRoute getRoute(HandelsPositie handelsPositie) {
        List<VraagAanbod> list = new ArrayList<>();
        if (this.parent != null) {
            list.addAll(this.parent.getRoute(handelsPositie).getVraagAanboden());
        }
        if (currentVraagAanbod != null) {
            list.add(this.currentVraagAanbod);
        }
        return new HandelsRoute(list, handelsPositie);
    }

    /**
     * Dit geeft een lijst van alle {@link HandelsRoute handelsroutes} die er berekent zijn. Het gaat door alle children
     * heen totdat hij de leaf children heeft gevonden, en zal vanaf daar aan de hand van {@link #getRoute(HandelsPositie)}
     * de {@link HandelsRoute} berekenen vanaf de leaf.
     *
     * @param handelsPositie De begin handelspositie
     * @return Geeft een lijst van alle handelsroutes terug
     */
    private List<HandelsRoute> getAllLeaves(HandelsPositie handelsPositie) {
        List<HandelsRoute> leaves = new ArrayList<>();

        for (Tree child : children) {
            if (child.isLeaf()) {
                HandelsRoute route = child.getRoute(handelsPositie);
                if (route.getVraagAanboden().size() > 0) {
                    leaves.add(route);
                }
            } else {
                leaves.addAll(child.getAllLeaves(handelsPositie));
            }
        }

        return leaves;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "currentStad=" + currentStad.getNaam() +
                ", parent=" + (parent != null ? parent.getCurrentStad().getNaam() : "null") +
                ", actionsLeft=" + actionsLeft +
                ", currentVraagAanbod=" + (currentVraagAanbod != null ? currentVraagAanbod : "null") +
                '}';
    }
}