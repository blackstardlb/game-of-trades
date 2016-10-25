package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;

/**
 * Dit is de test class voor het {@link io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmAccurate}
 *
 * @see HandelsplanAlgoritme
 * @see AbstractHandelsPlanAlgorithmTest
 * @see io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmAccurate
 */
public class HandelsPlanAlgorithmAccurateTest extends AbstractHandelsPlanAlgorithmTest {

    /**
     * Met deze method geven we een nieuwe instance van het {@link io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmAccurate}
     * terug
     *
     * @return Geef een nieuwe instance van het HandelsPlanAlgorithmAccurate
     * @see io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmAccurate
     */
    @Override
    public HandelsplanAlgoritme getHandelsplanAlgoritme() {
        return new HandelsPlanAlgorithmFast();
    }
}