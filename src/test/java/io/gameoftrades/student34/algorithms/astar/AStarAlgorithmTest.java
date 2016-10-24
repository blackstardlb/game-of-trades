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

public class AStarAlgorithmTest {
    private Handelaar handelaar;
    private AStarAlgorithm aStarAlgorithm;

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


    @Before
    public void setUp() throws Exception {
        handelaar = new HandelaarImpl();
        aStarAlgorithm = new AStarAlgorithm(false);
    }
}