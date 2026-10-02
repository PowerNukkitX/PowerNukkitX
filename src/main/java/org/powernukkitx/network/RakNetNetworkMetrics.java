package org.powernukkitx.network;

import org.cloudburstmc.netty.channel.raknet.config.RakChannelMetrics;
import org.powernukkitx.network.NetworkInterface.NetworkPressure;

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
    private static final float LOW_LOAD_RATIO = 0.01f;
    private static final float HIGH_LOAD_RATIO = 0.05f;
    private static final float BANDWIDTH_DECAY = 0.01f;

    private final AtomicLong bytesOutCurrentWindow = new AtomicLong();
    private volatile int queuedPacketBytes;
    private volatile int approximateMaxBps;
    private long previousWindowBytes;
    private long windowStartNanos = System.nanoTime();

    /**
     * Immutable RakNet traffic/pressure sample.
     *
     * @param bytesOutPerSecond approximate bytes sent during the rolling one-second window
     * @param queuedPacketBytes currently queued outbound RakNet bytes
     * @param estimatedBandwidthBps highest/decaying observed bandwidth estimate in bits per second
     * @param pressure classified network pressure
     */
    public record NetworkSnapshot(
            long bytesOutPerSecond,
            int queuedPacketBytes,
            int estimatedBandwidthBps,
            NetworkPressure pressure
    ) {
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
     * Returns one coherent traffic and pressure sample.
     *
     * @return current RakNet network sample
     */
    public synchronized NetworkSnapshot snapshot() {
        final long bytesOutPerSecond = this.getApproximateBytesSentLastSecond();
        final int actualBps = (int) Math.min(Integer.MAX_VALUE, bytesOutPerSecond * 8L);
        int estimate = Math.max(this.approximateMaxBps, actualBps);

        if (this.queuedPacketBytes > 0) {
            estimate += (int) ((actualBps - estimate) * BANDWIDTH_DECAY);
        }

        this.approximateMaxBps = Math.max(0, estimate);

        final int queuedBytes = this.queuedPacketBytes;
        final NetworkPressure pressure;

        if (queuedBytes <= 0) {
            pressure = NetworkPressure.UNRESTRICTED;
        } else if (this.approximateMaxBps <= 0) {
            pressure = NetworkPressure.HIGH;
        } else {
            final float queueRatio = queuedBytes / (this.approximateMaxBps * BITS_TO_BYTES);

            if (queueRatio <= LOW_LOAD_RATIO) {
                pressure = NetworkPressure.LOW;
            } else if (queueRatio <= HIGH_LOAD_RATIO) {
                pressure = NetworkPressure.MEDIUM;
            } else {
                pressure = NetworkPressure.HIGH;
            }
        }

        return new NetworkSnapshot(bytesOutPerSecond, queuedBytes, this.approximateMaxBps, pressure);
    }

    /**
     * Returns the current network pressure.
     *
     * @return current network pressure
     */
    public NetworkPressure getNetworkPressure() {
        return this.snapshot().pressure();
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
