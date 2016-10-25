package io.gameoftrades.student34.algorithms.stedentour;

/**
 * Deze class kan worden gebruikt als key voor bijvoorbeeld een {@link java.util.Map} of een {@link java.util.List}.
 *
 * @param <T>
 * @see CostCache
 */
public class DoubleMapKey<T> {

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
