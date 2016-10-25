package io.gameoftrades.student34.algorithms.handelsplan;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.algoritme.HandelsplanAlgoritme;
import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Richting;
import io.gameoftrades.model.lader.WereldLader;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.Handelsplan;
import io.gameoftrades.model.markt.actie.*;
import io.gameoftrades.student34.HandelaarImpl;
import io.gameoftrades.student34.utils.ReflectionUtil;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Dit is een abstracte test class voor het {@link HandelsplanAlgoritme}. We hebben deze classe abstract gemaakt
 * Omdat we meerdere implementaties van het {@link HandelsplanAlgoritme} hebben (namelijk {@link io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmFast}
 * en {@link io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmAccurate}. In plaats van dat we dezelfde test
 * twee keer schrijven voor deze twee implementaties hebben we een abstracte classe gemaakt waarin de implementatie van deze
 * test alleen hoeft te zeggen welke {@link HandelsplanAlgoritme} implementatie je wilt testen en de rest wordt vanzelf gedaan.
 *
 * @see HandelsplanAlgoritme
 * @see io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmAccurate
 * @see io.gameoftrades.student34.algorithms.handelsplan.HandelsPlanAlgorithmFast
 * @see HandelsPlanAlgorithmAccurateTest
 * @see HandelsPlanAlgorithmeFastTest
 */
public abstract class AbstractHandelsPlanAlgorithmTest {

    private HandelsplanAlgoritme handelsPlanAlgorithmFast;
    private WereldLader wereldLader = new HandelaarImpl().nieuweWereldLader();

    /**
     * Deze constructor is protected om ervoor te zorgen dat je deze class niet als test uit kan voeren.
     * Je moet alleen de implementaties van deze class als test kunnen uitvoeren.
     */
    protected AbstractHandelsPlanAlgorithmTest() {
    }

    /**
     * In deze method geef je aan welke {@link HandelsplanAlgoritme} je wilt testen.
     *
     * @return Geeft de implementatie terug die getest moet worden
     */
    public abstract HandelsplanAlgoritme getHandelsplanAlgoritme();

    @Before
    public void setup() {
        handelsPlanAlgorithmFast = getHandelsplanAlgoritme();
    }

    /**
     * Deze test kijkt wat er gebeurt als je het HandelsPlanAlgoritme uitvoert op een kaart waar maar 1 stad op stad en
     * dus geen mogelijke handelsplan op uitgevoerd kan worden. We verwachten dat er geen errors worden gegooid maar dat
     * er gewoon een lege lijst met acties wordt teruggeven.
     *
     * @throws Exception
     */
    @Test
    public void testBereken1Stad() throws Exception {
        Wereld wereld = wereldLader.laad("/kaarten/handelsplan/een-stad.txt");
        Handelsplan plan = handelsPlanAlgorithmFast.bereken(wereld, new HandelsPositie(wereld, wereld.getSteden().get(0), 100, 10, 50));
        assertThat(plan.getActies().size(), is(0));
    }


    /**
     * Deze test kijkt wat er gebeurt als er op een kaart wel meerdere steden zijn, maar geen handel bij deze steden
     * zijn ingesteld. We verwachten dat er geen errors worden gegooid maar dat er gewoon een lege lijst met acties
     * wordt teruggeven.
     *
     * @throws Exception
     */
    @Test
    public void testBerekenGeenHandel() throws Exception {
        Wereld wereld = wereldLader.laad("/kaarten/handelsplan/geen-handel.txt");
        Handelsplan plan = handelsPlanAlgorithmFast.bereken(wereld, new HandelsPositie(wereld, wereld.getSteden().get(0), 100, 10, 50));
        assertThat(plan.getActies().size(), is(0));
    }

    /**
     * Met deze test testen we of het HandelsPlanAlgoritme daadwerkelijk de meest optimale route berekent. We hebben
     * hiervoor een kaart gemaakt waarvan we van te voren hebben berekent wat de meest optimale route met het meeste winst is.
     * We hebben hiervoor een startkapitaal van 100$, een maximale draagcapaciteit van 10 en een maximaal aantal acties van 50
     * genomen.
     *
     * @throws Exception
     */
    @Test
    public void testWinst() throws Exception {
        Wereld wereld = wereldLader.laad("/kaarten/handelsplan/test-stad.txt");

        // Start kapitaal
        int startKapitaal = 100;
        int omzet = startKapitaal;

        int maxCapaciteit = 10;
        int capaciteit = maxCapaciteit;

        // Handmatig voorberekende optimale winst.
        int expected = 800;

        Handelsplan plan = handelsPlanAlgorithmFast.bereken(wereld, new HandelsPositie(wereld, wereld.getSteden().get(0), startKapitaal, maxCapaciteit, 50));
        for (Actie actie : plan.getActies()) {
            if (actie instanceof KoopActie) {
                KoopActie koopActie = (KoopActie) actie;
                Handel handel = (Handel) ReflectionUtil.get(koopActie, "handel");
                capaciteit -= Math.min(capaciteit, (omzet / handel.getPrijs()));
                omzet -= (maxCapaciteit - capaciteit) * handel.getPrijs();
            } else if (actie instanceof VerkoopActie) {
                VerkoopActie verkoopActie = (VerkoopActie) actie;
                Handel handel = (Handel) ReflectionUtil.get(verkoopActie, "handel");
                omzet += (maxCapaciteit - capaciteit) * handel.getPrijs();
                capaciteit = maxCapaciteit;
            }
        }

        assertThat(omzet - startKapitaal, is(expected));
    }

    /**
     * Met deze test testen we of het HandelsPlanAlgoritme daadwerkelijk het goede aantal acties berekent en uitvoert.
     * We hebben hiervoor een kaart gemaakt waarvan we van te voren hebben berekent wat de meest optimale route met het
     * meeste winst is en daarvan het aantal acties berekent die er dan genomen worden. We hebben hiervoor een startkapitaal
     * van 100$, een maximale draagcapaciteit van 10 en een maximaal aantal acties van 50 genomen.
     *
     * @throws Exception
     */
    @Test
    public void testAantalActies() throws Exception {
        Wereld wereld = wereldLader.laad("/kaarten/handelsplan/test-stad.txt");
        Handelsplan plan = handelsPlanAlgorithmFast.bereken(wereld, new HandelsPositie(wereld, wereld.getSteden().get(0), 100, 10, 50));

        /**
         * Voorberekend totaal aantal stappen voor de optimale route.
         * De optimale route (vanaf Aberdeen) is:
         * Aberdeen (koop 10 schapen) ->
         * Cambridge (verkoop 10 schapen, koop 10 stenen) ->
         * Birmingham (verkoop 10 stenen, koop niks) ->
         * Cambridge (koop 10 stenen) ->
         * Birmingham (verkoop 10 stenen)
         *
         * Het kost 15 stappen van Aberdeen naar Cambridge, en 10 van Cambridge naar Birminham, dus het lopen kost:
         * 15 + 10 + 10 + 10 = 45;
         *
         * Dan komen er nog 2 koop- en 2 veroopacties bij, dus:
         * 45 + 4 = 49;
         */
        int expected = 49;

        int totaalActies = plan.getActies().stream().mapToInt(actie -> {
            if (actie instanceof VerkoopActie || actie instanceof KoopActie) {
                return 1;
            } else if (actie instanceof NavigeerActie) {
                Coordinaat van = (Coordinaat) ReflectionUtil.get(NavigeerActie.class, actie, "van");
                Richting richting = (Richting) ReflectionUtil.get(NavigeerActie.class, actie, "richting");
                return wereld.getKaart().kijk(wereld.getKaart().getTerreinOp(van), richting).getTerreinType().getBewegingspunten();
            } else {
                return 0;
            }
        }).sum();

        assertThat(totaalActies, is(expected));
    }

}