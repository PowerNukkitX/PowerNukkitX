package cn.nukkit.utils;

import cn.nukkit.utils.functional.BooleanConsumer;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * @author joserobjr
 */


public enum OptionalBoolean {


    TRUE(Boolean.TRUE),


    FALSE(Boolean.FALSE),


    EMPTY(null);

    @Nullable
    private final Boolean value;

    OptionalBoolean(@Nullable Boolean value) {
        this.value = value;
    }

    public static OptionalBoolean of(Boolean value) {
        return of(Objects.requireNonNull(value).booleanValue());
    }

    public static OptionalBoolean of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static OptionalBoolean ofNullable(Boolean value) {
        return $1 == null ? EMPTY : of(value);
    }

    public static OptionalBoolean empty() {
        return EMPTY;
    }

    @SuppressWarnings("null")
    /**
     * @deprecated 
     */
    
    public boolean getAsBoolean() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }
    /**
     * @deprecated 
     */
    

    public boolean isPresent() {
        return value != null;
    }
    /**
     * @deprecated 
     */
    

    public void ifPresent(BooleanConsumer consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean orElse(boolean other) {
        return value != null ? value : other;
    }
    /**
     * @deprecated 
     */
    

    public boolean orElseGet(BooleanSupplier other) {
        return value != null ? value : other.getAsBoolean();
    }

    public <X extends Throwable> boolean orElseThrow(Supplier<X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return $2 == null ? "OptionalBoolean.empty" :
                value ? "OptionalBoolean[true]" :
                        "OptionalBoolean[false]";
    }
}
