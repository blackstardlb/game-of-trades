package io.gameoftrades.student34;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Richting;

/**
 * Deze class wordt alleen gebruikt om aan te geven dat er geen mogelijke route is tussen twee steden.
 *
 * @see io.gameoftrades.model.algoritme.SnelstePadAlgoritme
 * @see io.gameoftrades.student34.algorithms.astar.AStarAlgorithm
 * @see io.gameoftrades.student34.algorithms.stedentour.CostCache
 * @see Pad
 */
public class NullPad implements Pad {

    /**
     * We geven de maximale int terug, om ervoor te zorgen dat deze paden bij het vergelijken van andere paden altijd
     * als langzaamste uitkomt (behalve als het pad waarmee vergeleken wordt ook een NullPad is, natuurlijk)
     *
     * @return de totale tijd (in bewegingspunten) die dit pad kost.
     * @see Integer#MAX_VALUE
     */
    @Override
    public int getTotaleTijd() {
        return Integer.MAX_VALUE;
    }

    /**
     * Deze method geeft een lege {@link Richting} array terug, omdat er bij een NullPad natuurlijk nooit bewegingen zijn
     * omdat dit pad alleen bestaat als er geen route mogelijk is.
     *
     * @return de bewegingen waaruit het pad bestaat.
     */
    @Override
    public Richting[] getBewegingen() {
        return new Richting[0];
    }

    /**
     * Deze method geeft zichzelf terug, omdat er geen bewegingen zijn die omgekeerd kunnen worden.
     *
     * @return het pad in omgekeerde richting.
     */
    @Override
    public Pad omgekeerd() {
        return this;
    }

    /**
     * Deze method geeft altijd het start coordinaat weer terug, omdat je een NullPad natuurlijk nooit kunt volgen.
     *
     * @param start het start coordinaat.
     * @return het eindpunt van het pad. Dit is hier altijd het beginpunt.
     */
    @Override
    public Coordinaat volg(Coordinaat start) {
        return start;
    }
}
