package cn.nukkit.level.generator.noise.minecraft.utils;

import java.util.Objects;

public final class Triplet<A, B, C> {
    private final A a;
    private final B b;
    private final C c;

    public Triplet(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Triplet(Triplet<? extends A, ? extends B, ? extends C> other) {
        this(other.a, other.b, other.c);
    }

    public A getFirst() {
        return this.a;
    }

    public B getSecond() {
        return this.b;
    }

    public C getThird() { return this.c;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triplet)) return false;
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return Objects.equals(a, triplet.a) && Objects.equals(b, triplet.b) && Objects.equals(c, triplet.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c);
    }

    public String toString() {
        return "(" + this.a + ", " + this.b + ", " + this.c + ")";
    }
}