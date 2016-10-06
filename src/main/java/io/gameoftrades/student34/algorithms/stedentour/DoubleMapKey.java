package io.gameoftrades.student34.algorithms.stedentour;

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
