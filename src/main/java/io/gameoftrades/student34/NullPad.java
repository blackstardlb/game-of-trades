package io.gameoftrades.student34;

import io.gameoftrades.model.kaart.Coordinaat;
import io.gameoftrades.model.kaart.Pad;
import io.gameoftrades.model.kaart.Richting;

public class NullPad implements Pad {

    @Override
    public int getTotaleTijd() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Richting[] getBewegingen() {
        return new Richting[0];
    }

    @Override
    public Pad omgekeerd() {
        return this;
    }

    @Override
    public Coordinaat volg(Coordinaat start) {
        return start;
    }
}
