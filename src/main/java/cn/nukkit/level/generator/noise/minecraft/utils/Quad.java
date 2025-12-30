package cn.nukkit.level.generator.noise.minecraft.utils;

import java.util.Objects;

public final class Quad<A, B, C, D> {

    private final A a;
    private final B b;
    private final C c;
    private final D d;

    public Quad(A a, B b, C c, D d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public Quad(Quad<? extends A, ? extends B, ? extends C, ? extends D> other) {
        this(other.a, other.b, other.c, other.d);
    }

    public A getFirst() {
        return this.a;
    }

    public B getSecond() {
        return this.b;
    }

    public C getThird() { return this.c;}

    public D getFourth() { return this.d;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quad)) return false;
        Quad<?, ?, ?, ?> quad = (Quad<?, ?, ?, ?>) o;
        return Objects.equals(a, quad.a) && Objects.equals(b, quad.b) && Objects.equals(c, quad.c) && Objects.equals(d, quad.d);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, d);
    }

    public String toString() {
        return "(" + this.a + ", " + this.b + ", " + this.c + ", " + this.d + ")";
    }
}