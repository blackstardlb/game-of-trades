package io.gameoftrades.student34.heuristic;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Terrein;

public abstract class Heuristic {
    public abstract double berekenH(Terrein terrein, Coordinaat coordinaatEind);

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
