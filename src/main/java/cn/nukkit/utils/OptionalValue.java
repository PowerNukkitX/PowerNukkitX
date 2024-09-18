package cn.nukkit.utils;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;


public final class OptionalValue<T> {
    private static final OptionalValue<?> EMPTY = new OptionalValue<>(null);

    @Nullable
    private final T value;

    OptionalValue(@Nullable T value) {
        this.value = value;
    }

    public static <T> OptionalValue<T> of(T value) {
        return new OptionalValue<>(Objects.requireNonNull(value));
    }

    public static <T> OptionalValue<T> ofNullable(T value) {
        return value == null ? OptionalValue.empty() : of(value);
    }

    public static <T> OptionalValue<T> empty() {
        return (OptionalValue<T>) EMPTY;
    }

    public boolean isPresent() {
        return value != null;
    }

    public void ifPresent(Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public T get() {
        return value;
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }

    public T orElseGet(Supplier<T> other) {
        return value != null ? value : other.get();
    }

    public <X extends Throwable> T orElseThrow(Supplier<X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public String toString() {
        return value == null ? "OptionalValue.empty" : "OptionalValue[" + value + "]";
    }
}
