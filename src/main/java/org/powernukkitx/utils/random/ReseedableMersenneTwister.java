package org.powernukkitx.utils.random;

import org.apache.commons.rng.core.source32.IntProvider;
import org.apache.commons.rng.core.util.NumberFactory;

import java.util.Arrays;

/**
 * Reseedable Mersenne Twister matching Commons RNG 1.7 MT long-seed behavior without rebuilding the provider.
 *
 * @author Curse
 */
final class ReseedableMersenneTwister extends IntProvider {

    private static final long INT_MASK = 0xffffffffL;
    private static final long GOLDEN_RATIO = 0x9e3779b97f4a7c15L;
    private static final int UPPER_MASK = 0x80000000;
    private static final int LOWER_MASK = 0x7fffffff;
    private static final int MATRIX_A = 0x9908b0df;
    private static final int N = 624;
    private static final int M = 397;
    private static final int[] INITIAL_STATE = createInitialState();

    private final int[] state = new int[N];
    private int index;

    ReseedableMersenneTwister(long seed) {
        reseed(seed);
    }

    void reseed(long seed) {
        System.arraycopy(INITIAL_STATE, 0, state, 0, N);

        long splitMixState = seed == -GOLDEN_RATIO ? ~seed : seed;
        long splitMixValue = 0L;

        for (int seedIndex = 0; seedIndex < N - 1; seedIndex++) {
            int seedValue;
            if ((seedIndex & 1) == 0) {
                splitMixState += GOLDEN_RATIO;
                splitMixValue = stafford13(splitMixState);
                seedValue = (int) splitMixValue;
            } else {
                seedValue = (int) (splitMixValue >>> 32);
            }

            mixSeed(seedIndex + 1, seedValue, seedIndex);
        }

        state[0] = state[N - 1];
        mixSeed(1, (int) (splitMixValue >>> 32), N - 1);

        for (int stateIndex = 2; stateIndex < N; stateIndex++) {
            mixState(stateIndex);
        }

        state[0] = state[N - 1];
        mixState(1);

        state[0] = UPPER_MASK;
        index = N;
        resetCachedState();
    }

    private void mixSeed(int stateIndex, int seedValue, int seedIndex) {
        int previous = state[stateIndex - 1];
        state[stateIndex] = (state[stateIndex] ^ ((previous ^ (previous >>> 30)) * 1664525)) + seedValue + seedIndex;
    }

    private void mixState(int stateIndex) {
        int previous = state[stateIndex - 1];
        state[stateIndex] = (state[stateIndex] ^ ((previous ^ (previous >>> 30)) * 1566083941)) - stateIndex;
    }

    private static int[] createInitialState() {
        int[] initialState = new int[N];
        long value = 19650218L;
        initialState[0] = (int) value;

        for (int i = 1; i < N; i++) {
            value = (1812433253L * (value ^ (value >>> 30)) + i) & INT_MASK;
            initialState[i] = (int) value;
        }

        return initialState;
    }

    private static long stafford13(long value) {
        value = (value ^ (value >>> 30)) * 0xbf58476d1ce4e5b9L;
        value = (value ^ (value >>> 27)) * 0x94d049bb133111ebL;
        return value ^ (value >>> 31);
    }

    @Override
    public int next() {
        if (index >= N) {
            for (int i = 0; i < N - M; i++) {
                int value = (state[i] & UPPER_MASK) | (state[i + 1] & LOWER_MASK);
                state[i] = state[i + M] ^ (value >>> 1) ^ (MATRIX_A & -(value & 1));
            }

            for (int i = N - M; i < N - 1; i++) {
                int value = (state[i] & UPPER_MASK) | (state[i + 1] & LOWER_MASK);
                state[i] = state[i + M - N] ^ (value >>> 1) ^ (MATRIX_A & -(value & 1));
            }

            int value = (state[N - 1] & UPPER_MASK) | (state[0] & LOWER_MASK);
            state[N - 1] = state[M - 1] ^ (value >>> 1) ^ (MATRIX_A & -(value & 1));
            index = 0;
        }

        int value = state[index++];
        value ^= value >>> 11;
        value ^= (value << 7) & 0x9d2c5680;
        value ^= (value << 15) & 0xefc60000;
        value ^= value >>> 18;
        return value;
    }

    @Override
    protected byte[] getStateInternal() {
        int[] savedState = Arrays.copyOf(state, N + 1);
        savedState[N] = index;
        return composeStateInternal(NumberFactory.makeByteArray(savedState), super.getStateInternal());
    }

    @Override
    protected void setStateInternal(byte[] savedState) {
        byte[][] parts = splitStateInternal(savedState, (N + 1) * Integer.BYTES);
        int[] restoredState = NumberFactory.makeIntArray(parts[0]);
        System.arraycopy(restoredState, 0, state, 0, N);
        index = restoredState[N];
        super.setStateInternal(parts[1]);
    }
}
