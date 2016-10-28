package io.gameoftrades.student34.algorithms.astar;

import io.gameoftrades.model.Handelaar;
import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.student34.HandelaarImpl;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * Dit is de test class voor het {@link AStarAlgorithm A* algoritme}. We testen hier of onze implementatie wel daadwerkelijk werkt.
 *
 * @see io.gameoftrades.model.algoritme.SnelstePadAlgoritme
 * @see AStarAlgorithm
 */
public class AStarAlgorithmTest {
    private Handelaar handelaar;
    private AStarAlgorithm aStarAlgorithm;

    /**
     * Deze test kijkt of ons {@link AStarAlgorithm A* algoritme} wel daadwerkelijk de snelste route berekent. We hebben hiervoor
     * berekent wat de snelste route tussen Aberdeen en Birmingham is (deze steden staan in de voorbeeld kaart). Daarna
     * berekenen we of het berekende aantal stappen van het {@link AStarAlgorithm A* algoritme} overeenkomt met onze
     * voorberekende getal.
     *
     * @throws Exception
     */
    @Test
    public void bereken() throws Exception {
        int expectedTotalTime = 19;
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        Stad from = wereld.getSteden().get(0);
        Stad to = wereld.getSteden().get(1);

        Pad pad = aStarAlgorithm.bereken(wereld.getKaart(), from.getCoordinaat(), to.getCoordinaat());
        assertThat("NullCheck pad", pad, is(notNullValue()));
        assertThat("Pad expected time", expectedTotalTime, equalTo(pad.getTotaleTijd()));

        Pad padReversed = aStarAlgorithm.bereken(wereld.getKaart(), to.getCoordinaat(), from.getCoordinaat());
        assertThat("NullCheck reverse pad", pad, is(notNullValue()));
        assertThat("Reverse pad expected time", expectedTotalTime, equalTo(padReversed.getTotaleTijd()));
    }

    /**
     * Met deze test kijken we wat er gebeurt als je de kortste route tussen dezelfde stad probeert te berekenen.
     *
     * @throws Exception
     */
    @Test
    public void berekenZelfdeStad() throws Exception {
        int expectedTotalTime = 0;
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/voorbeeld-kaart.txt");
        Stad from = wereld.getSteden().get(0);
        Stad to = wereld.getSteden().get(0);

        Pad pad = aStarAlgorithm.bereken(wereld.getKaart(), from.getCoordinaat(), to.getCoordinaat());
        assertThat("NullCheck pad", pad, is(notNullValue()));
        assertThat("Pad expected time", expectedTotalTime, equalTo(pad.getTotaleTijd()));
    }

    @Before
    public void setUp() throws Exception {
        handelaar = new HandelaarImpl();
        aStarAlgorithm = new AStarAlgorithm(false);
    }
}