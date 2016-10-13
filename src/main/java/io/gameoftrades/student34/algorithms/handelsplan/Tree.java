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

    public Stad getCurrentStad() {
        return currentStad;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public VraagAanbod getCurrentVraagAanbod() {
        return currentVraagAanbod;
    }

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

    public boolean isLeaf() {
        return children.size() == 0;
    }

    public HandelsRoute getBest(HandelsPositie handelsPositie) {
        PriorityQueue<HandelsRoute> handelsRoutes = new PriorityQueue<>(getAllLeaves(handelsPositie));
        return handelsRoutes.poll();
    }

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

    public void print() {
        System.out.println(this);
        for (Tree tree : children) {
            tree.print();
        }
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