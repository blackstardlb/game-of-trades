package io.gameoftrades.student34.algorithms.stedentour;

import io.gameoftrades.model.Handelaar;
import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.student34.HandelaarImpl;
import io.gameoftrades.student34.algorithms.astar.AStarAlgorithm;
import io.gameoftrades.student34.utils.ReflectionUtil;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * In deze class wordt de {@link CostCache} class getest
 *
 * @see CostCache
 */
public class CostCacheTest {

    private Handelaar handelaar;

    @Before
    public void setUp() throws Exception {
        handelaar = new HandelaarImpl();
        CostCache.clearCache();
    }

    /**
     * Deze test checkt of de cache wel echt werkt door eerst een route op te vragen die nog niet berekend is (dus deze zal worden berekend) en daarna
     * dezelfde route nog een keer op te vragen en te kijken of het de tweede keer sneller is.
     *
     * @throws Exception
     */
    @Test
    public void getPath() throws Exception {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/westeros-kaart.txt");
        Stad from = wereld.getSteden().get(0);
        Stad to = wereld.getSteden().get(wereld.getSteden().size() - 1);
        SnelstePadAlgoritme snelstePadAlgoritme = handelaar.nieuwSnelstePadAlgoritme();
        Pad expected = snelstePadAlgoritme.bereken(wereld.getKaart(), from.getCoordinaat(), to.getCoordinaat());

        long beginTime = System.currentTimeMillis();
        Pad actualCalculated = CostCache.getPath(wereld.getKaart(), from, to);
        long calculatedDuration = System.currentTimeMillis() - beginTime;
        assertThat("Actual Calculated", expected.getTotaleTijd(), is(actualCalculated.getTotaleTijd()));
        assertArrayEquals(expected.getBewegingen(), actualCalculated.getBewegingen());

        beginTime = System.currentTimeMillis();
        Pad actualCached = CostCache.getPath(wereld.getKaart(), from, to);
        long cachedDuration = System.currentTimeMillis() - beginTime;
        assertThat("Actual Cached", expected.getTotaleTijd(), is(actualCached.getTotaleTijd()));
        assertArrayEquals(expected.getBewegingen(), actualCached.getBewegingen());

        beginTime = System.currentTimeMillis();
        Pad actualCachedReversed = CostCache.getPath(wereld.getKaart(), to, from);
        long reverseCachedDuration = System.currentTimeMillis() - beginTime;
        expected = expected.omgekeerd();
        assertThat("ActualCachedReversed", expected.getTotaleTijd(), is(actualCachedReversed.getTotaleTijd()));
        assertArrayEquals(expected.getBewegingen(), actualCachedReversed.getBewegingen());

        assertThat("Saved Time", cachedDuration, is(lessThan(calculatedDuration)));
        assertThat("Reverse saved Time", reverseCachedDuration, is(lessThan(calculatedDuration)));
    }

    /**
     * Deze test checkt of de cache wel echt werkt door eerst de cost van een route op te vragen die nog niet berekend is (dus deze zal worden berekend) en daarna
     * dezelfde cost nog een keer op te vragen en te kijken of het de tweede keer sneller is.
     *
     * @throws Exception
     */
    @Test
    public void getCost() throws Exception {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/westeros-kaart.txt");
        Stad from = wereld.getSteden().get(0);
        Stad to = wereld.getSteden().get(wereld.getSteden().size() - 1);
        SnelstePadAlgoritme snelstePadAlgoritme = handelaar.nieuwSnelstePadAlgoritme();
        int expected = snelstePadAlgoritme.bereken(wereld.getKaart(), from.getCoordinaat(), to.getCoordinaat()).getTotaleTijd();

        long beginTime = System.currentTimeMillis();
        int actualCalculated = CostCache.getCost(wereld.getKaart(), from, to);
        long calculatedDuration = System.currentTimeMillis() - beginTime;
        assertThat("Actual Calculated", expected, is(actualCalculated));

        beginTime = System.currentTimeMillis();
        int actualCached = CostCache.getCost(wereld.getKaart(), from, to);
        long cachedDuration = System.currentTimeMillis() - beginTime;
        assertThat("Actual Cached", expected, is(actualCached));

        beginTime = System.currentTimeMillis();
        int actualCachedReversed = CostCache.getCost(wereld.getKaart(), to, from);
        long reverseCachedDuration = System.currentTimeMillis() - beginTime;
        assertThat("ActualCachedReversed", expected, is(actualCachedReversed));


        assertThat("Saved Time", cachedDuration, is(lessThan(calculatedDuration)));
        assertThat("Reverse saved Time", reverseCachedDuration, is(lessThan(calculatedDuration)));
    }

    /**
     * Deze test checkt of de {@link CostCache#clearCache()} method werkt door te kijken wat de size van de cache is, daarna de {@link CostCache#clearCache()}
     * uit te voeren en daarna te kijken of de cache leeg is.
     *
     * @throws Exception
     */
    @SuppressWarnings("all")
    @Test
    public void clearCache() throws Exception {
        Wereld wereld = handelaar.nieuweWereldLader().laad("/kaarten/westeros-kaart.txt");
        Stad from = wereld.getSteden().get(0);
        Stad to = wereld.getSteden().get(wereld.getSteden().size() - 1);

        CostCache.getCost(wereld.getKaart(), from, to);

        HashMap<?, ?> map = (HashMap<?, ?>) ReflectionUtil.get(CostCache.class, null, "costs");
        assertThat(map, is(notNullValue()));
        assertThat("Map is filled", map.size(), is(not(0)));
        CostCache.clearCache();
        assertThat("Map is empty", map.size(), is(0));

    }
}