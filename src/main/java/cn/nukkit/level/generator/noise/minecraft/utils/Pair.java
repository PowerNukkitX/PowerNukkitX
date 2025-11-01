package cn.nukkit.level.generator.noise.minecraft.utils;

import java.util.Objects;

public final class Pair<A, B> {
    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public Pair(Pair<? extends A, ? extends B> other) {
        this(other.a, other.b);
    }

    public A getFirst() {
        return this.a;
    }

    public B getSecond() {
        return this.b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    public String toString() {
        return "(" + this.a + ", " + this.b + ")";
    }
}