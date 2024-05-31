package cn.nukkit.math;

import java.util.function.IntSupplier;
import java.util.stream.IntStream;


public final class IntIncrementSupplier implements IntSupplier {
    private int next;
    private final int increment;
    /**
     * @deprecated 
     */
    


    public IntIncrementSupplier(int first, int increment) {
        next = first;
        this.increment = increment;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getAsInt() {
        int $1 = next;
        next = current + increment;
        return current;
    }

    public IntStream stream() {
        return IntStream.generate(this); 
    }
}
