package jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Covers this round's findings: the density-function marker state lookup, the shaped-recipe scan
 * that rebuilt its candidate set per call, shapeless matching's per-candidate list copy, the
 * double map probe in inventory reads, and the chunk-column range used by entity queries.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 8, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class LookupAndCloneBenchmark {

    // --- DensityCommon marker state lookup ---
    /** Representative overworld density chain length. */
    private static final int MARKER_COUNT = 12;

    static final class Marker {
        final int slot;
        Marker(int slot) { this.slot = slot; }
    }

    private Marker[] markers;
    private Map<Marker, Object> identityStates;
    private Object[] arrayStates;
    private final ThreadLocal<Object> threadLocalState = ThreadLocal.withInitial(Object::new);

    // --- Recipe lookup ---
    /** Roughly the shaped-recipe count of a vanilla registry. */
    private static final int SHAPED_RECIPES = 900;
    private Map<Integer, Set<Object>> shapedBuckets;

    // --- Shapeless match ---
    private List<Integer> ingredients;
    private int[] inputItems;

    // --- Inventory slot read ---
    private Map<Integer, Object> slots;
    private int[] slotProbes;

    @Setup
    public void setup() {
        Random random = new Random(5150L);

        markers = new Marker[MARKER_COUNT];
        identityStates = new IdentityHashMap<>();
        arrayStates = new Object[MARKER_COUNT];
        for (int i = 0; i < MARKER_COUNT; i++) {
            markers[i] = new Marker(i);
            Object state = new Object();
            identityStates.put(markers[i], state);
            arrayStates[i] = state;
        }

        shapedBuckets = new HashMap<>();
        for (int i = 0; i < SHAPED_RECIPES; i++) {
            shapedBuckets.computeIfAbsent(1 + random.nextInt(9), k -> new HashSet<>()).add(new Object());
        }

        ingredients = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ingredients.add(i);
        }
        inputItems = new int[]{4, 2, 0, 3, 1};

        slots = new HashMap<>();
        for (int i = 0; i < 36; i++) {
            slots.put(i, new Object());
        }
        slotProbes = new int[64];
        for (int i = 0; i < slotProbes.length; i++) {
            slotProbes[i] = random.nextInt(48);   // some misses, as an inventory scan has
        }
    }

    // -----------------------------------------------------------------
    // Marker state: identity map probe vs dense array index
    // -----------------------------------------------------------------

    @Benchmark
    public void markerStateIdentityMap(Blackhole hole) {
        for (Marker marker : markers) {
            hole.consume(identityStates.computeIfAbsent(marker, ignored -> new Object()));
        }
    }

    @Benchmark
    public void markerStateDenseArray(Blackhole hole) {
        for (Marker marker : markers) {
            Object state = arrayStates[marker.slot];
            if (state == null) {
                state = new Object();
                arrayStates[marker.slot] = state;
            }
            hole.consume(state);
        }
    }

    @Benchmark
    public void markerStateThreadLocal(Blackhole hole) {
        for (int i = 0; i < MARKER_COUNT; i++) {
            hole.consume(threadLocalState.get());
        }
    }

    // -----------------------------------------------------------------
    // Shaped recipe lookup: rebuild the candidate set vs walk the buckets
    // -----------------------------------------------------------------

    @Benchmark
    public Object shapedLookupRebuildSet() {
        Set<Object> all = new HashSet<>();
        for (Set<Object> bucket : shapedBuckets.values()) {
            all.addAll(bucket);
        }
        for (Object recipe : all) {
            if (recipe.hashCode() == 0) return recipe;   // stand-in for match(), essentially never true
        }
        return null;
    }

    @Benchmark
    public Object shapedLookupWalkBuckets() {
        for (Set<Object> bucket : shapedBuckets.values()) {
            for (Object recipe : bucket) {
                if (recipe.hashCode() == 0) return recipe;
            }
        }
        return null;
    }

    // -----------------------------------------------------------------
    // Shapeless match: copy the ingredient list vs a consumed bitmask
    // -----------------------------------------------------------------

    @Benchmark
    public boolean shapelessMatchListCopy() {
        List<Integer> remaining = new ArrayList<>(ingredients);
        for (int item : inputItems) {
            boolean matched = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (remaining.get(i) == item) {
                    remaining.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }
        return remaining.isEmpty();
    }

    @Benchmark
    public boolean shapelessMatchBitmask() {
        int count = ingredients.size();
        long consumed = 0L;
        int remaining = count;
        for (int item : inputItems) {
            boolean matched = false;
            for (int i = 0; i < count; i++) {
                if ((consumed & (1L << i)) != 0) continue;
                if (ingredients.get(i) == item) {
                    consumed |= 1L << i;
                    remaining--;
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }
        return remaining == 0;
    }

    // -----------------------------------------------------------------
    // Inventory slot read: containsKey + get vs a single get
    // -----------------------------------------------------------------

    @Benchmark
    public void slotReadDoubleProbe(Blackhole hole) {
        for (int index : slotProbes) {
            hole.consume(slots.containsKey(index) ? slots.get(index) : null);
        }
    }

    @Benchmark
    public void slotReadSingleProbe(Blackhole hole) {
        for (int index : slotProbes) {
            hole.consume(slots.get(index));
        }
    }

    // -----------------------------------------------------------------
    // Entity query chunk range: ceil vs floor on the max chunk index
    // -----------------------------------------------------------------

    /**
     * Player-sized box grown by (1, 0.5, 1), as {@code Player.checkNearEntities} builds it. The
     * cost is not the bound arithmetic - it is how many chunk columns of entities get walked, so
     * these benchmarks scan a simulated column set rather than just computing the bounds.
     */
    private static final double MIN_X = 6.7, MAX_X = 9.3, MIN_Z = 6.7, MAX_Z = 9.3;

    /** Entities per chunk near an item pile or mob farm. */
    @Param({"400"})
    public int entitiesPerColumn;

    private double[][] columnEntityY;
    /** The same entities, bucketed into 24 sections of 16 blocks. */
    private double[][][] sectionBuckets;

    @Setup(Level.Trial)
    public void setupColumns() {
        Random random = new Random(9001L);
        columnEntityY = new double[16][entitiesPerColumn];
        sectionBuckets = new double[16][24][];
        for (int c = 0; c < columnEntityY.length; c++) {
            List<List<Double>> buckets = new ArrayList<>();
            for (int i = 0; i < 24; i++) buckets.add(new ArrayList<>());
            for (int i = 0; i < entitiesPerColumn; i++) {
                double y = random.nextDouble() * 384 - 64;
                columnEntityY[c][i] = y;
                int section = Math.min(23, Math.max(0, ((int) Math.floor(y) >> 4) + 4));
                buckets.get(section).add(y);
            }
            for (int i = 0; i < 24; i++) {
                sectionBuckets[c][i] = buckets.get(i).stream().mapToDouble(Double::doubleValue).toArray();
            }
        }
    }

    private int scan(int minX, int maxX, int minZ, int maxZ) {
        int hits = 0;
        int column = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (double y : columnEntityY[column & 15]) {
                    if (y >= MIN_X && y <= MAX_X) hits++;
                }
                column++;
            }
        }
        return hits;
    }

    @Benchmark
    public int nearbyEntityScanCeilBound() {
        return scan((int) Math.floor((MIN_X - 2) / 16), (int) Math.ceil((MAX_X + 2) / 16),
                (int) Math.floor((MIN_Z - 2) / 16), (int) Math.ceil((MAX_Z + 2) / 16));
    }

    /**
     * With the Y-section index: only the buckets overlapping the query height are visited. The
     * query pads one section low because entities are bucketed by the position of their feet.
     */
    @Benchmark
    public int nearbyEntityScanSectionIndexed() {
        int minX = (int) Math.floor((MIN_X - 2) / 16);
        int maxX = (int) Math.floor((MAX_X + 2) / 16);
        int minZ = (int) Math.floor((MIN_Z - 2) / 16);
        int maxZ = (int) Math.floor((MAX_Z + 2) / 16);
        int hits = 0;
        int column = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                double[][] buckets = sectionBuckets[column & 15];
                // 24 sections in the overworld; a 3-block tall query spans one, plus one below.
                for (int section = 3; section <= 4; section++) {
                    for (double y : buckets[section]) {
                        if (y >= MIN_X && y <= MAX_X) hits++;
                    }
                }
                column++;
            }
        }
        return hits;
    }

    @Benchmark
    public int nearbyEntityScanFloorBound() {
        return scan((int) Math.floor((MIN_X - 2) / 16), (int) Math.floor((MAX_X + 2) / 16),
                (int) Math.floor((MIN_Z - 2) / 16), (int) Math.floor((MAX_Z + 2) / 16));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LookupAndCloneBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
