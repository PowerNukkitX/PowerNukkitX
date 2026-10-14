package jmh;

import org.powernukkitx.entity.ai.route.data.Node;
import org.powernukkitx.level.format.bitarray.BitArray;
import org.powernukkitx.level.format.bitarray.BitArrayVersion;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.SortedList;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Covers the per-tick hot paths that can be exercised without booting a server: the bounding-box
 * block scan every entity runs, the A* open-list probe, the nearest-entity reduction, the behavior
 * group timers, the block-id normalisation on the random-tick path, and the palette bit array.
 * <p>
 * Each pair is the shape the code had before the performance pass followed by the shape it has now.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 8, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class TickHotPathBenchmark {

    // --- Entity bounding-box block scan ---
    private AxisAlignedBB[] hitboxes;

    // --- A* open list membership probe ---
    private PriorityQueue<Node> openList;
    private HashMap<Vector3, Node> openIndex;
    private Vector3 openProbe;

    // --- Vector3 as a HashSet key (the A* close list) ---
    private HashSet<Vector3> closeSet;
    private Vector3[] closeProbes;

    // --- NearestTargetEntitySensor reduction ---
    private double[] candidateDistances;

    // --- BehaviorGroup period timers ---
    private Map<Object, Integer> boxedTimers;
    private Object[] behaviorArray;
    private int[] intTimers;

    // --- Block.isTickingDisabled identifier normalisation ---
    private List<String> disabledIds;
    private String[] normalizedDisabledIds;
    private String probeId;

    // --- Palette bit array ---
    private BitArray bitArray;

    @Setup
    public void setup() {
        Random random = new Random(20260825L);

        hitboxes = new AxisAlignedBB[512];
        for (int i = 0; i < hitboxes.length; i++) {
            double x = random.nextDouble() * 64;
            double y = 60 + random.nextDouble() * 8;
            double z = random.nextDouble() * 64;
            hitboxes[i] = new SimpleAxisAlignedBB(x - 0.3, y, z - 0.3, x + 0.3, y + 1.8, z + 0.3);
        }

        openList = new PriorityQueue<>();
        openIndex = new HashMap<>();
        for (int i = 0; i < 600; i++) {
            Vector3 position = new Vector3(random.nextInt(64), random.nextInt(64) - 64, random.nextInt(64));
            Node node = new Node(position, null, random.nextInt(1000), random.nextInt(1000));
            openList.offer(node);
            openIndex.put(position, node);
        }
        openProbe = new Vector3(999, 999, 999);

        closeSet = new HashSet<>();
        closeProbes = new Vector3[256];
        for (int i = 0; i < 4096; i++) {
            closeSet.add(new Vector3(random.nextInt(32), random.nextInt(384) - 64, random.nextInt(32)));
        }
        for (int i = 0; i < closeProbes.length; i++) {
            closeProbes[i] = new Vector3(random.nextInt(32), random.nextInt(384) - 64, random.nextInt(32));
        }

        candidateDistances = new double[30];
        for (int i = 0; i < candidateDistances.length; i++) {
            candidateDistances[i] = random.nextDouble() * 256;
        }

        behaviorArray = new Object[12];
        boxedTimers = new HashMap<>();
        for (int i = 0; i < behaviorArray.length; i++) {
            behaviorArray[i] = new Object();
            boxedTimers.put(behaviorArray[i], 0);
        }
        intTimers = new int[behaviorArray.length];

        disabledIds = List.of("minecraft:flowing_water", "minecraft:flowing_lava", "minecraft:fire",
                "minecraft:sand", "minecraft:gravel", "minecraft:tnt");
        normalizedDisabledIds = new String[]{"water", "lava", "fire", "sand", "gravel", "tnt"};
        probeId = "minecraft:stone";

        bitArray = BitArrayVersion.V2.createArray(4096);
        for (int i = 0; i < 4096; i++) {
            bitArray.set(i, random.nextInt(4));
        }
    }

    // -----------------------------------------------------------------
    // Bounding-box block scan: ceil + inclusive bound over-scans by a full layer per axis
    // -----------------------------------------------------------------

    @Benchmark
    public int boundingBoxScanCeilBound() {
        int visited = 0;
        for (AxisAlignedBB bb : hitboxes) {
            int minX = NukkitMath.floorDouble(bb.getMinX());
            int minY = NukkitMath.floorDouble(bb.getMinY());
            int minZ = NukkitMath.floorDouble(bb.getMinZ());
            int maxX = NukkitMath.ceilDouble(bb.getMaxX());
            int maxY = NukkitMath.ceilDouble(bb.getMaxY());
            int maxZ = NukkitMath.ceilDouble(bb.getMaxZ());
            for (int z = minZ; z <= maxZ; ++z)
                for (int x = minX; x <= maxX; ++x)
                    for (int y = minY; y <= maxY; ++y) visited++;
        }
        return visited;
    }

    @Benchmark
    public int boundingBoxScanFloorBound() {
        int visited = 0;
        for (AxisAlignedBB bb : hitboxes) {
            int minX = NukkitMath.floorDouble(bb.getMinX());
            int minY = NukkitMath.floorDouble(bb.getMinY());
            int minZ = NukkitMath.floorDouble(bb.getMinZ());
            int maxX = NukkitMath.floorDouble(bb.getMaxX());
            int maxY = NukkitMath.floorDouble(bb.getMaxY());
            int maxZ = NukkitMath.floorDouble(bb.getMaxZ());
            for (int z = minZ; z <= maxZ; ++z)
                for (int x = minX; x <= maxX; ++x)
                    for (int y = minY; y <= maxY; ++y) visited++;
        }
        return visited;
    }

    // -----------------------------------------------------------------
    // A* open list: full iteration vs an index alongside the queue
    // -----------------------------------------------------------------

    @Benchmark
    public Node openListLinearScan() {
        for (Node node : openList) {
            if (openProbe.equals(node.getVector3())) return node;
        }
        return null;
    }

    @Benchmark
    public Node openListIndexed() {
        return openIndex.get(openProbe);
    }

    // -----------------------------------------------------------------
    // Vector3 as a HashSet key: the old hash kept only the low 8 bits of y
    // -----------------------------------------------------------------

    @Benchmark
    public int closeListLookup(Blackhole hole) {
        int hits = 0;
        for (Vector3 probe : closeProbes) {
            if (closeSet.contains(probe)) hits++;
        }
        hole.consume(hits);
        return hits;
    }

    @Benchmark
    public void vector3HashSpread(Blackhole hole) {
        for (Vector3 probe : closeProbes) {
            hole.consume(probe.hashCode());
        }
    }

    // -----------------------------------------------------------------
    // Nearest-entity reduction: a balanced tree built to read element 0
    // -----------------------------------------------------------------

    @Benchmark
    public Double nearestViaSortedList() {
        List<Double> sorted = new SortedList<>(Comparator.naturalOrder());
        for (double distance : candidateDistances) {
            sorted.add(distance);
        }
        return sorted.isEmpty() ? null : sorted.get(0);
    }

    @Benchmark
    public double nearestViaSinglePass() {
        double nearest = Double.MAX_VALUE;
        for (double distance : candidateDistances) {
            if (distance < nearest) nearest = distance;
        }
        return nearest;
    }

    // -----------------------------------------------------------------
    // BehaviorGroup timers: boxed map entries vs a parallel int array
    // -----------------------------------------------------------------

    @Benchmark
    public void behaviorTimersBoxed(Blackhole hole) {
        var evalSucceed = new HashSet<Object>(behaviorArray.length);
        for (Map.Entry<Object, Integer> entry : boxedTimers.entrySet()) {
            int tick = entry.getValue();
            int nextTick = ++tick;
            boxedTimers.put(entry.getKey(), nextTick);
            if (nextTick < 4) continue;
            boxedTimers.put(entry.getKey(), 0);
            evalSucceed.add(entry.getKey());
        }
        hole.consume(evalSucceed);
    }

    @Benchmark
    public int behaviorTimersIntArray() {
        int succeeded = 0;
        for (int i = 0; i < behaviorArray.length; i++) {
            int nextTick = ++intTimers[i];
            if (nextTick < 4) continue;
            intTimers[i] = 0;
            succeeded++;
        }
        return succeeded;
    }

    // -----------------------------------------------------------------
    // Block.isTickingDisabled: renormalising the config list on every call
    // -----------------------------------------------------------------

    @Benchmark
    public boolean tickingDisabledNormalisePerCall() {
        String normalizedId = probeId.toLowerCase();
        if (normalizedId.startsWith("minecraft:")) normalizedId = normalizedId.substring(10);
        if (normalizedId.startsWith("flowing_")) normalizedId = normalizedId.substring(8);
        for (String disabledId : disabledIds) {
            String normalizedDisabled = disabledId.toLowerCase();
            if (normalizedDisabled.startsWith("minecraft:")) normalizedDisabled = normalizedDisabled.substring(10);
            if (normalizedDisabled.startsWith("flowing_")) normalizedDisabled = normalizedDisabled.substring(8);
            if (normalizedId.equals(normalizedDisabled)) return true;
        }
        return false;
    }

    @Benchmark
    public boolean tickingDisabledCachedRegionMatches() {
        int offset = 0;
        if (probeId.regionMatches(true, 0, "minecraft:", 0, 10)) offset = 10;
        if (probeId.regionMatches(true, offset, "flowing_", 0, 8)) offset += 8;
        int length = probeId.length() - offset;
        for (String candidate : normalizedDisabledIds) {
            if (candidate.length() == length && probeId.regionMatches(true, offset, candidate, 0, length)) return true;
        }
        return false;
    }

    // -----------------------------------------------------------------
    // Palette bit array: the read behind every block lookup
    // -----------------------------------------------------------------

    @Benchmark
    public void bitArrayGet(Blackhole hole) {
        for (int i = 0; i < 4096; i += 16) {
            hole.consume(bitArray.get(i));
        }
    }

    @Benchmark
    public List<Integer> paletteScanAllocating() {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 4096; i += 16) {
            values.add(bitArray.get(i));
        }
        return values;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TickHotPathBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
