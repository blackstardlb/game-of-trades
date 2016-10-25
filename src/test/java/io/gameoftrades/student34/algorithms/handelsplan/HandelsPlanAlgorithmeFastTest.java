package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;

/**
 * Dit is de test class voor het {@link io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmFast}
 *
 * @see HandelsplanAlgoritme
 * @see AbstractHandelsPlanAlgorithmTest
 * @see io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmFast
 */
public class HandelsPlanAlgorithmeFastTest extends AbstractHandelsPlanAlgorithmTest {

    /**
     * Met deze method geven we een nieuwe instance van het {@link io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmFast}
     * terug
     *
     * @return Geef een nieuwe instance van het HandelsPlanAlgorithmFast
     * @see io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmFast
     */
    @Override
    public HandelsplanAlgoritme getHandelsplanAlgoritme() {
        return new HandelsPlanAlgorithmFast();
    }
}
