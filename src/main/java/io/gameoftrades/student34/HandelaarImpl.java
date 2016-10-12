package io.gameoftrades.student34;

import io.gameoftrades.model.Handelaar;
import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.lader.WereldLader;
import io.gameoftrades.student34.algorithms.astar.AStarAlgorithm;
import io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmAccurate;
import io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmFast;
import io.gameoftrades.student34.algorithms.stedentour.StedenTourAlgorithm;

/**
 * Welkom bij Game of Trades!
 * <p>
 * Voordat er begonnen kan worden moet eerst de 'student34' package omgenoemd worden
 * zodat iedere groep zijn eigen namespace heeft. Vervang de NN met je groep nummer.
 * Dus als je in groep 3 zit dan wordt de packagenaam 'student03' en ben je in groep
 * 42 dan wordt de package naam 'student42'.
 * <p>
 * Om te controleren of je het goed hebt gedaan is er de ProjectSanityTest die je kan draaien.
 */
public class HandelaarImpl implements Handelaar {

    /**
     * Opdracht 1, zie ook de handige test-set in WereldLaderImplTest.
     */
    @Override
    public WereldLader nieuweWereldLader() {
        return new WereldLaderImpl();
    }

    /**
     * Opdracht 2
     */
    @Override
    public SnelstePadAlgoritme nieuwSnelstePadAlgoritme() {
        return new AStarAlgorithm(true);
    }

    /**
     * Opdracht 3
     */
    @Override
    public StedenTourAlgoritme nieuwStedenTourAlgoritme() {
        return new StedenTourAlgorithm();
    }

    /**
     * Opdracht 4
     */
    @Override
    public HandelsplanAlgoritme nieuwHandelsplanAlgoritme() {
        return new HandelsPlanAlgorithmAccurate();
    }
}
