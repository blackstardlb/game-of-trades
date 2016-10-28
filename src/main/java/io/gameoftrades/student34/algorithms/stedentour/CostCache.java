package io.gameoftrades.student34.algorithms.stedentour;

import io.gameoftrades.model.algoritme.SnelstePadAlgoritme;
import io.gameoftrades.model.kaart.Kaart;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Stad;
import io.gameoftrades.student34.algorithms.astar.AStarAlgorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * We gebruiken de CostCache class om paden die we bereken (bij bijvoorbeeld {@link StedenTourAlgorithm}) te cachen.
 * We doen dit omdat we bij onze algoritmes vaak een route die we al een keer berekent hebben nog een keer 'berekenen'.
 * Doordat we dit cachen hoeft het systeem niet opnieuw de hele route te berekenen en kan hij het pad opvragen die opgeslagen
 * is, dit helpt de performance heel erg veel.
 *
 * @see Pad
 * @see SnelstePadAlgoritme
 * @see AStarAlgorithm
 */
public class CostCache {

    /**
     * De map die onze berekende paden bijhoudt, oftewel: onze cache. We gebruiken de {@link DoubleMapKey} om aan te geven
     * welk pad tussen welke twee steden past.
     *
     * @see Pad
     * @see DoubleMapKey
     * @see StadWrapper
     */
    private static final Map<DoubleMapKey<StadWrapper>, Pad> costs = new HashMap<>();

    /**
     * Het algoritme dat we gebruiken om de paden te berekenen die we later opslaan.
     *
     * @see AStarAlgorithm
     * @see SnelstePadAlgoritme
     */
    private static final SnelstePadAlgoritme snelstePadAlgoritme = new AStarAlgorithm(false);

    /**
     * Met deze method bereken je de kortste route tussen twee opgegeven steden, en als ze al een keer berekend
     * zijn worden ze uit de {@link CostCache#costs cache} gehaald.
     *
     * @param kaart De kaart waar de steden op staan
     * @param stad1 De beginstad
     * @param stad2 De eindstad
     * @return Geeft de kortste route tussen de twee opgegeven steden.
     * @see Pad
     * @see Stad
     * @see Kaart
     */
    public static Pad getPath(Kaart kaart, Stad stad1, Stad stad2) {
        DoubleMapKey<StadWrapper> key = new DoubleMapKey<>(new StadWrapper(stad1), new StadWrapper(stad2));
        // Kijk of het pad tussen de twee steden al een keer berekend is, en geef deze terug als hij al berekend is.
        if (costs.containsKey(key)) {
            return costs.get(key);
        } else {
            // Als de kortste route nog niet berekend is, bereken hem dan aan de hand van het SnelstePadAlgoritme
            Pad pad = snelstePadAlgoritme.bereken(kaart, stad1.getCoordinaat(), stad2.getCoordinaat());

            // Sla de berekende route op in de cache
            costs.put(key, pad);

            /**
             * Sla de omgekeerde route (dus de route van de eindstad naar de beginstad) op, dit doen we omdat het dan tijd
             * scheelt als we de omgekeerde route proberen te krijgen, dan hoeft hij het niet te berekenen omdat we hem al
             * hebben opgeslagen
             */
            costs.put(new DoubleMapKey<>(new StadWrapper(stad2), new StadWrapper(stad1)), pad.omgekeerd());
            return pad;
        }
    }

    /**
     * Met deze method krijg je het aantal stappen dat het kost om tussen twee opgegeven steden te reizen. Als de route al
     * berekend is haalt het het pad uit de cache.
     *
     * @param kaart De kaart waar de steden op staan.
     * @param stad1 De beginstad
     * @param stad2 De eindstad
     * @return Geeft de kosten van de kortste route tussen de twee opgegeven steden.
     * @see CostCache#getPath(Kaart, Stad, Stad)
     * @see Kaart
     * @see Stad
     */
    public static int getCost(Kaart kaart, Stad stad1, Stad stad2) {
        return getPath(kaart, stad1, stad2).getTotaleTijd();
    }

    /**
     * Deze method verwijder alles uit onze cache, dit is vooral bedoed voor testen, en zal niet in de applicatie zelf
     * gebruikt worden.
     *
     * @see HashMap#clear()
     */
    public static void clearCache() {
        costs.clear();
    }

    /**
     * We gebruiken deze StadWrapper class omdat de {@link Stad} class geen hashcode en equals method override, dus onze
     * cache tests werkte niet correct. In deze class maken we onze eigen hashcode en equals functie die ervoor zullen zorgen
     * dat de cache werkt.
     *
     * @see Stad
     */
    public static class StadWrapper {

        /**
         * De stad die 'ge-wrapped' wordt.
         */
        private final Stad stad;

        public StadWrapper(Stad stad) {
            this.stad = stad;
        }

        @Override
        public String toString() {
            return stad.toString();
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof StadWrapper && obj.toString().equalsIgnoreCase(toString());
        }
    }

    /**
     * Deze class kan worden gebruikt als key voor bijvoorbeeld een {@link java.util.Map} of een {@link java.util.List}.
     *
     * @param <T>
     * @see CostCache
     */
    private static class DoubleMapKey<T> {

        private final T x;
        private final T y;

        DoubleMapKey(T x, T y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof DoubleMapKey) {
                DoubleMapKey key = (DoubleMapKey) o;
                return (this.x.equals(key.x) && this.y.equals(key.y));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = result * 37 + x.hashCode();
            return result * 37 + y.hashCode();
        }
    }
}
