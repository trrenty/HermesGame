package com.hermes.util;

public class Pair<A, B> {
    private final A first;
    private final B second;
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }
    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        return first.equals(pair.first);
    }

    @Override
    public int hashCode() {
        return first.hashCode();
    }
}
