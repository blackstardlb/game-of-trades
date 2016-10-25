package io.gameoftrades.student34.algorithms.stedentour;

import io.gameoftrades.model.Handelaar;
import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.algoritme.StedenTourAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.student34.HandelaarImpl;
import io.gameoftrades.student34.WereldLaderImpl;
import io.gameoftrades.student34.algorithms.astar.AStarAlgorithm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by Wouter on 25-10-2016.
 */
public class StedenTourAlgorithmTest {
    private Handelaar handelaar;

    @Before
    public void setUp() throws Exception {
        handelaar = new HandelaarImpl();
    }

    /**
     *
     * We doen hier een test waar we testen of de berekende route de meeste geoptimaliseerde route is, de beste stedentour
     * @throws Exception
     */

    @Test
    public void bereken() throws Exception {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        StedenTourAlgoritme stedenTourAlgoritme = handelaar.nieuwStedenTourAlgoritme();
        List<Coordinaat> bereken = stedenTourAlgoritme.bereken(wereld.getKaart(), wereld.getSteden()).stream().map(Stad::getCoordinaat).collect(Collectors.toList());

        List<Coordinaat> expected = new ArrayList<>();
        expected.add(Coordinaat.op(7, 1));
        expected.add(Coordinaat.op(8, 6));
        expected.add(Coordinaat.op(1, 7));
        expected.add(Coordinaat.op(4, 1));

        List<Coordinaat> expected2 = new ArrayList<>();
        for (int i = expected.size() - 1; i >= 0; i--) {
            expected2.add(expected.get(i));
        }

        assertThat(bereken, is(anyOf(equalTo(expected), equalTo(expected2))));
    }


    /**
     *
     *
     * @throws Exception
     */

    @Test
    public void berekenEenStad() throws Exception {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        StedenTourAlgoritme stedenTourAlgoritme = handelaar.nieuwStedenTourAlgoritme();

        List<Stad> eenStad = new ArrayList<>();
        eenStad.add(wereld.getSteden().get(0));

        List<Stad> bereken = stedenTourAlgoritme.bereken(wereld.getKaart(), eenStad);
        int expectedArraySize = 1;
        Stad expectedStad = wereld.getSteden().get(0);
        assertThat(expectedArraySize, is(bereken.size()));
        assertThat(expectedStad.getCoordinaat(), is(bereken.get(0).getCoordinaat()));
    }

    @Test(expected = NullPointerException.class)
    public void geenStad() throws Exception {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        StedenTourAlgoritme stedenTourAlgoritme = handelaar.nieuwStedenTourAlgoritme();

        List<Stad> geenStad = new ArrayList<>();

        stedenTourAlgoritme.bereken(wereld.getKaart(), geenStad);
    }
}