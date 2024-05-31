package cn.nukkit.math;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;


public final class AtomicIntIncrementSupplier implements IntSupplier {
    private final AtomicInteger next;
    private final int increment;
    /**
     * @deprecated 
     */
    


    public AtomicIntIncrementSupplier(int first, int increment) {
        next = new AtomicInteger(first);
        this.increment = increment;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getAsInt() {
        return next.getAndAdd(increment);
    }

    public IntStream stream() {
        return IntStream.generate(this); 
    }
}
