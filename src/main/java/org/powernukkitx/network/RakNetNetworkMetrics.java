package org.powernukkitx.network;

import org.cloudburstmc.netty.channel.raknet.config.RakChannelMetrics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks the RakNet connection load used to throttle chunk publishing.
 *
 * @author Curse
 */
public final class RakNetNetworkMetrics implements RakChannelMetrics {
    private static final long BANDWIDTH_WINDOW_NANOS = TimeUnit.SECONDS.toNanos(1);
    private static final float BITS_TO_BYTES = 0.125f;
    private static final float HIGH_LOAD_RATIO = 0.05f;
    private static final float BANDWIDTH_DECAY = 0.01f;

    private final AtomicLong bytesOutCurrentWindow = new AtomicLong();
    private volatile int queuedPacketBytes;
    private volatile int approximateMaxBps;
    private long previousWindowBytes;
    private long windowStartNanos = System.nanoTime();

    /**
     * Classifies the current RakNet network pressure.
     *
     * @author Curse
     */
    public enum NetworkLoad {
        UNRESTRICTED,
        LOW,
        MEDIUM,
        HIGH
    }

    @Override
    public void bytesOut(int count) {
        if (count > 0) this.bytesOutCurrentWindow.addAndGet(count);
    }

    @Override
    public void queuedPacketBytes(int count) {
        this.queuedPacketBytes = Math.max(0, count);
    }

    /**
     * Returns the current network load.
     * @return the requested value
     */
    public NetworkLoad getNetworkLoad() {
        this.updateBandwidthEstimate();
        final int queuedBytes = this.queuedPacketBytes;

        if (queuedBytes <= 0) return NetworkLoad.LOW;

        final int maxBps = this.approximateMaxBps;
        if (maxBps <= 0) return NetworkLoad.HIGH;

        final float queueRatio = queuedBytes / (maxBps * BITS_TO_BYTES);

        return queueRatio > HIGH_LOAD_RATIO ? NetworkLoad.HIGH : NetworkLoad.MEDIUM;
    }

    private synchronized void updateBandwidthEstimate() {
        final long actualBytesSent = this.getApproximateBytesSentLastSecond();
        final int actualBps = (int) Math.min(Integer.MAX_VALUE, actualBytesSent * 8L);
        int estimate = Math.max(this.approximateMaxBps, actualBps);

        if (this.queuedPacketBytes > 0) {
            estimate += (int) ((actualBps - estimate) * BANDWIDTH_DECAY);
        }

        this.approximateMaxBps = Math.max(0, estimate);
    }

    private long getApproximateBytesSentLastSecond() {
        final long now = System.nanoTime();
        long elapsed = now - this.windowStartNanos;

        if (elapsed >= BANDWIDTH_WINDOW_NANOS) {
            final long completedWindow = this.bytesOutCurrentWindow.getAndSet(0L);
            final long elapsedWindows = elapsed / BANDWIDTH_WINDOW_NANOS;
            this.previousWindowBytes = elapsedWindows == 1 ? completedWindow : 0L;
            this.windowStartNanos += elapsedWindows * BANDWIDTH_WINDOW_NANOS;
            elapsed = now - this.windowStartNanos;
        }

        final long currentWindowBytes = this.bytesOutCurrentWindow.get();
        final double previousWindowWeight = 1.0 - ((double) elapsed / BANDWIDTH_WINDOW_NANOS);

        return currentWindowBytes + (long) (this.previousWindowBytes * previousWindowWeight);
    }
}
