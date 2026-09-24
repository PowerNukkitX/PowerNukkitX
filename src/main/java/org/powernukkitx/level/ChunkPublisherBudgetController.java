package org.powernukkitx.level;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.network.NetworkInterface.NetworkPressure;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.sun.management.OperatingSystemMXBean;

import org.jetbrains.annotations.ApiStatus;

/**
 * Dynamically allocates LevelChunk publication capacity from server network and runtime headroom.
 *
 * @author Curse
 */
public final class ChunkPublisherBudgetController {
    private static final long RESOURCE_SAMPLE_INTERVAL_NANOS = 250_000_000L;
    private static final long STREAMER_TIMEOUT_NANOS = 500_000_000L;
    private static final double LINK_UTILIZATION_TARGET = 0.80d;
    private static final double TOKEN_BUCKET_SECONDS = 0.25d;
    private static final double DEFAULT_ACCOUNTED_CHUNK_BYTES = 32d * 1024d;
    private static final double MIN_SAMPLED_CHUNK_BYTES = 1024d;
    private static final double MAX_SAMPLED_CHUNK_BYTES = 1024d * 1024d;
    private static final double CHUNK_BYTES_EWMA_ALPHA = 0.10d;
    private static final double GC_LIVE_SET_PRESSURE_START = 0.80d;
    private static final double GC_LIVE_SET_PRESSURE_HARD = 0.90d;
    private static final double GC_OVERHEAD_PRESSURE_START = 0.05d;
    private static final double GC_OVERHEAD_PRESSURE_HARD = 0.20d;
    private static final double MEMORY_EMERGENCY_MIN_FACTOR = 0.15d;
    private static final int FALLBACK_BASE_BUDGET = 40;

    private final Server server;
    private final OperatingSystemMXBean operatingSystem;
    private final List<MemoryPoolMXBean> memoryPools;
    private final List<GarbageCollectorMXBean> garbageCollectors;
    private final Map<Integer, StreamerState> activeStreamers = new HashMap<>();

    private long lastResourceSampleNanos;
    private long lastStreamerCleanupNanos;
    private long lastTokenRefillNanos;
    private long lastGcSampleNanos;
    private long lastGcCollectionCount = -1L;
    private long lastGcCollectionTimeMillis = -1L;
    private long upstreamBytesPerSecond;
    private double averageChunkBytes = DEFAULT_ACCOUNTED_CHUNK_BYTES;
    private double resourceFactor = 1d;
    private double targetChunkBytesPerSecond;
    private double byteTokens;
    private int dynamicBaseBudget = FALLBACK_BASE_BUDGET;

    /**
     * Creates the server-global chunk publication budget controller.
     *
     * @param server owning server
     */
    public ChunkPublisherBudgetController(Server server) {
        this.server = Preconditions.checkNotNull(server, "server");
        var bean = ManagementFactory.getOperatingSystemMXBean();
        this.operatingSystem = bean instanceof OperatingSystemMXBean osBean ? osBean : null;
        this.memoryPools = ManagementFactory.getMemoryPoolMXBeans();
        this.garbageCollectors = ManagementFactory.getGarbageCollectorMXBeans();
    }

    /**
     * Reserves a fair chunk-send grant for one active streamer.
     *
     * @param player player requesting publication capacity
     * @param backlog ready chunks waiting for this player
     * @param networkPressure transport-specific peer network pressure
     * @return chunks allowed in this publisher pass
     */
    @ApiStatus.Internal
    public synchronized int acquireChunkSendBudget(
            Player player,
            int backlog,
            NetworkPressure networkPressure
    ) {
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(networkPressure, "networkPressure");
        Preconditions.checkArgument(backlog >= 0, "backlog must be >= 0");
        if (backlog == 0) return 0;

        final long now = System.nanoTime();
        refreshResourceSnapshot(now);
        cleanupInactiveStreamers(now);

        final StreamerState streamer = activeStreamers.computeIfAbsent(player.getLoaderId(), ignored -> new StreamerState());
        streamer.lastSeenNanos = now;

        final int networkDivisor = switch (networkPressure) {
            case UNKNOWN, UNRESTRICTED -> 1;
            case LOW -> 2;
            case MEDIUM -> 4;
            case HIGH -> 8;
        };

        final int activeCount = Math.max(1, activeStreamers.size());
        final double fairShare = (double) dynamicBaseBudget / activeCount / networkDivisor;
        final double creditCap = Math.max(1d, fairShare * 4d);
        streamer.chunkCredits = Math.min(creditCap, streamer.chunkCredits + fairShare);

        refillTokens(now);

        final double accountedChunkBytes = getAccountedChunkBytes();
        final int creditBudget = floorToInt(streamer.chunkCredits);
        final int tokenBudget = floorToInt(byteTokens / accountedChunkBytes);
        final int grant = Math.min(backlog, Math.min(creditBudget, tokenBudget));

        if (grant <= 0) return 0;

        streamer.chunkCredits -= grant;
        byteTokens -= grant * accountedChunkBytes;
        return grant;
    }

    /**
     * Returns reserved capacity for chunks that were not sent.
     *
     * @param player player that received the grant
     * @param unusedChunks unused chunk slots
     */
    @ApiStatus.Internal
    public synchronized void releaseUnusedChunkBudget(Player player, int unusedChunks) {
        if (unusedChunks <= 0) return;

        StreamerState streamer = activeStreamers.get(player.getLoaderId());
        if (streamer != null) {
            streamer.chunkCredits += unusedChunks;
        }

        byteTokens = Math.min(getTokenCapacity(), byteTokens + unusedChunks * getAccountedChunkBytes());
    }

    /**
     * Records the serialized LevelChunk payload size for future budget estimation.
     *
     * @param payloadBytes serialized payload bytes
     */
    @ApiStatus.Internal
    public synchronized void recordChunkPayloadBytes(int payloadBytes) {
        if (payloadBytes <= 0) return;

        final double sample = clamp(payloadBytes, MIN_SAMPLED_CHUNK_BYTES, MAX_SAMPLED_CHUNK_BYTES);
        averageChunkBytes += (sample - averageChunkBytes) * CHUNK_BYTES_EWMA_ALPHA;
    }

    /**
     * Returns the current unrestricted global chunks-per-pass base.
     */
    @ApiStatus.Internal
    public synchronized int getDynamicBaseBudget() {
        return dynamicBaseBudget;
    }

    /**
     * Returns the number of recently active chunk streamers.
     */
    @ApiStatus.Internal
    public synchronized int getActiveStreamerCount() {
        return activeStreamers.size();
    }

    /**
     * Returns the current chunk-stream byte target per second.
     */
    @ApiStatus.Internal
    public synchronized long getTargetChunkBytesPerSecond() {
        return Math.max(0L, (long) targetChunkBytesPerSecond);
    }

    /**
     * Returns the current average serialized LevelChunk payload size.
     */
    @ApiStatus.Internal
    public synchronized int getAverageChunkPayloadBytes() {
        return Math.max(1, (int) Math.round(averageChunkBytes));
    }

    private void refreshResourceSnapshot(long now) {
        if (lastResourceSampleNanos != 0 && now - lastResourceSampleNanos < RESOURCE_SAMPLE_INTERVAL_NANOS) return;
        lastResourceSampleNanos = now;

        upstreamBytesPerSecond = resolveUpstreamBytesPerSecond();

        final double normalResourceFactor = calculateResourceFactor();
        final double memoryEmergencyFactor = calculateMemoryEmergencyFactor(now);
        final double desiredFactor = Math.min(normalResourceFactor, memoryEmergencyFactor);

        if (desiredFactor < resourceFactor) {
            resourceFactor = desiredFactor;
        } else {
            resourceFactor += (desiredFactor - resourceFactor) * 0.20d;
        }

        final double passesPerSecond = getConfiguredPassesPerSecond();
        final double accountedChunkBytes = getAccountedChunkBytes();

        if (upstreamBytesPerSecond > 0) {
            targetChunkBytesPerSecond = upstreamBytesPerSecond * LINK_UTILIZATION_TARGET * resourceFactor;
        } else {
            targetChunkBytesPerSecond = FALLBACK_BASE_BUDGET * accountedChunkBytes * passesPerSecond * resourceFactor;
        }

        dynamicBaseBudget = Math.max(
                1,
                floorToInt(targetChunkBytesPerSecond / passesPerSecond / accountedChunkBytes)
        );

        byteTokens = Math.min(byteTokens, getTokenCapacity());
    }

    private double calculateResourceFactor() {
        final double targetTps = getConfiguredPassesPerSecond();
        final double currentTps = server.getTicksPerSecondAverage();
        final double tpsRatio = clamp(currentTps / targetTps, 0.10d, 1d);
        final double tpsFactor = tpsRatio >= 0.98d ? 1d : Math.max(0.15d, tpsRatio);

        final double tickUsage = clamp(server.getTickUsageAverage(), 0d, 1d);
        final double tickFactor = pressureFactor(tickUsage, 0.65d, 0.95d, 0.15d);

        double cpuFactor = 1d;
        if (operatingSystem != null) {
            final double cpuLoad = operatingSystem.getProcessCpuLoad();
            if (cpuLoad >= 0d) {
                cpuFactor = pressureFactor(clamp(cpuLoad, 0d, 1d), 0.70d, 0.95d, 0.20d);
            }
        }

        return Math.min(Math.min(tpsFactor, tickFactor), cpuFactor);
    }

    private double calculateMemoryEmergencyFactor(long now) {
        long collectionCount = 0L;
        long collectionTimeMillis = 0L;
        boolean collectionCountAvailable = false;
        boolean collectionTimeAvailable = false;

        for (GarbageCollectorMXBean collector : garbageCollectors) {
            final long count = collector.getCollectionCount();
            final long timeMillis = collector.getCollectionTime();

            if (count >= 0L) {
                collectionCount += count;
                collectionCountAvailable = true;
            }

            if (timeMillis >= 0L) {
                collectionTimeMillis += timeMillis;
                collectionTimeAvailable = true;
            }
        }

        if (!collectionCountAvailable || !collectionTimeAvailable) {
            return 1d;
        }

        if (lastGcSampleNanos == 0L
                || lastGcCollectionCount < 0L
                || lastGcCollectionTimeMillis < 0L) {
            lastGcSampleNanos = now;
            lastGcCollectionCount = collectionCount;
            lastGcCollectionTimeMillis = collectionTimeMillis;
            return 1d;
        }

        final long elapsedNanos = Math.max(1L, now - lastGcSampleNanos);
        final long collectionCountDelta = Math.max(0L, collectionCount - lastGcCollectionCount);
        final long collectionTimeDeltaMillis = Math.max(0L, collectionTimeMillis - lastGcCollectionTimeMillis);

        lastGcSampleNanos = now;
        lastGcCollectionCount = collectionCount;
        lastGcCollectionTimeMillis = collectionTimeMillis;

        /*
         * Collection usage represents post-GC state. Do not derive memory
         * pressure from ordinary pre-GC heap occupancy.
         */
        if (collectionCountDelta == 0L || collectionTimeDeltaMillis == 0L) {
            return 1d;
        }

        long postGcUsedBytes = 0L;
        boolean collectionUsageAvailable = false;

        for (MemoryPoolMXBean memoryPool : memoryPools) {
            if (memoryPool.getType() != MemoryType.HEAP) {
                continue;
            }

            final MemoryUsage collectionUsage = memoryPool.getCollectionUsage();

            if (collectionUsage == null || collectionUsage.getUsed() < 0L) {
                continue;
            }

            postGcUsedBytes += collectionUsage.getUsed();
            collectionUsageAvailable = true;
        }

        final long maxHeapBytes = Runtime.getRuntime().maxMemory();

        if (!collectionUsageAvailable || maxHeapBytes <= 0L) {
            return 1d;
        }

        final double liveSetRatio = clamp(
                (double) postGcUsedBytes / maxHeapBytes,
                0d,
                1d
        );

        final double gcOverhead = clamp(
                collectionTimeDeltaMillis * 1_000_000d / elapsedNanos,
                0d,
                1d
        );

        final double liveSetFactor = pressureFactor(
                liveSetRatio,
                GC_LIVE_SET_PRESSURE_START,
                GC_LIVE_SET_PRESSURE_HARD,
                MEMORY_EMERGENCY_MIN_FACTOR
        );

        final double gcOverheadFactor = pressureFactor(
                gcOverhead,
                GC_OVERHEAD_PRESSURE_START,
                GC_OVERHEAD_PRESSURE_HARD,
                MEMORY_EMERGENCY_MIN_FACTOR
        );

        /*
         * Memory is an emergency guard, not a normal sizing input.
         * Both a retained post-GC live set and sustained GC overhead must
         * indicate pressure before chunk publication is reduced.
         */
        return Math.max(liveSetFactor, gcOverheadFactor);
    }

    private long resolveUpstreamBytesPerSecond() {
        final int upstreamMbps = server.getSettings().networkSettings().upstreamBandwidth();
        if (upstreamMbps <= 0) return 0L;

        return upstreamMbps * 1_000_000L / 8L;
    }

    private void cleanupInactiveStreamers(long now) {
        if (lastStreamerCleanupNanos != 0 && now - lastStreamerCleanupNanos < RESOURCE_SAMPLE_INTERVAL_NANOS) return;
        lastStreamerCleanupNanos = now;

        Iterator<Map.Entry<Integer, StreamerState>> iterator = activeStreamers.entrySet().iterator();
        while (iterator.hasNext()) {
            if (now - iterator.next().getValue().lastSeenNanos > STREAMER_TIMEOUT_NANOS) {
                iterator.remove();
            }
        }
    }

    private void refillTokens(long now) {
        final double tokenCapacity = getTokenCapacity();

        if (lastTokenRefillNanos == 0L) {
            lastTokenRefillNanos = now;
            byteTokens = Math.min(tokenCapacity, targetChunkBytesPerSecond / getConfiguredPassesPerSecond());
            return;
        }

        final double elapsedSeconds = Math.max(0d, (now - lastTokenRefillNanos) / 1_000_000_000d);
        lastTokenRefillNanos = now;
        byteTokens = Math.min(tokenCapacity, byteTokens + elapsedSeconds * targetChunkBytesPerSecond);
    }

    private double getTokenCapacity() {
        return Math.max(getAccountedChunkBytes(), targetChunkBytesPerSecond * TOKEN_BUCKET_SECONDS);
    }

    private double getAccountedChunkBytes() {
        return Math.max(DEFAULT_ACCOUNTED_CHUNK_BYTES, averageChunkBytes);
    }

    private double getConfiguredPassesPerSecond() {
        return Math.max(1d, 1_000_000_000d / Math.max(1L, server.getNanosPerTick()));
    }

    private static double pressureFactor(double value, double start, double hard, double minimum) {
        if (value <= start) return 1d;
        if (value >= hard) return minimum;

        final double progress = (value - start) / (hard - start);
        return 1d - progress * (1d - minimum);
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static int floorToInt(double value) {
        if (value <= 0d) return 0;
        if (value >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) Math.floor(value);
    }

    private static final class StreamerState {
        private long lastSeenNanos;
        private double chunkCredits;
    }
}
