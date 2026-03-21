package cn.nukkit.network.security;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

/**
 * Detects coordinated flood attacks by tracking per-session packet rates and
 * correlating multiple independent signals across all currently flooding sessions.
 *
 * <h3>Architecture</h3>
 * <ul>
 *   <li>Sessions are registered/unregistered at connect/disconnect time.</li>
 *   <li>{@link #recordPacket} is called from the Netty I/O thread for every inbound packet.</li>
 *   <li>{@link #tick()} is called once per server tick from the main thread, evaluates
 *       all signals, and returns a {@link BotnetReport} when a botnet is detected.</li>
 * </ul>
 *
 * <h3>Signals evaluated</h3>
 * <ol>
 *   <li><b>Volume</b>: N+ sessions exceeding the per-second packet threshold simultaneously.</li>
 *   <li><b>Timing</b>: All flood onsets started within 2 seconds of each other.</li>
 *   <li><b>Packet type</b>: 70%+ of sessions share the same dominant packet type (same script).</li>
 *   <li><b>Subnet</b>: 50%+ of sessions share the same /24 (VPS botnet).</li>
 *   <li><b>Fresh sessions</b>: 70%+ of sessions are younger than 30 seconds.</li>
 * </ol>
 *
 * <h3>Thread safety</h3>
 * {@link #recordPacket} and {@link #registerSession}/{@link #unregisterSession} are
 * called from Netty I/O threads and use per-session synchronization.
 * {@link #tick()} runs on the main thread and reads volatile flood-state fields.
 */
@Slf4j
public final class BotnetDetector {
    private static final long WINDOW_MS              = 1_000L;   // rolling window length
    private static final long ALERT_COOLDOWN_MS      = 10_000L;  // min gap between alerts
    private static final long TIMING_WINDOW_MS       = 2_000L;   // max spread of flood onsets
    private static final long SESSION_FRESH_MS       = 30_000L;  // "fresh" if connected < 30s ago
    private static final double PACKET_TYPE_AGREE    = 0.70;     // fraction that must share packet type
    private static final double SUBNET_OVERLAP       = 0.50;     // fraction from same /24
    private static final double FRESH_SESSION_RATIO  = 0.70;     // fraction that are fresh

    /**
     * Per-session statistics. One instance per connected session.
     * Window state is guarded by {@code synchronized(this)}.
     * Flood state fields are {@code volatile} for safe cross-thread visibility.
     */
    static final class SessionStats {
        final InetSocketAddress address;
        final long connectedAtMs;

        long windowStartMs;
        long windowCount;

        // Boyer-Moore majority-vote candidate for dominant packet type within the window.
        // Efficient O(1) approximation: correct when one type accounts for >50% of packets.
        int  dominantTypeCandidate;
        long dominantTypeScore;

        // Flood state; volatile so tick() on the main thread sees Netty-thread writes
        volatile boolean flooding;
        volatile long    floodOnsetMs;
        volatile int     floodPacketType;

        SessionStats(InetSocketAddress address, long nowMs) {
            this.address       = address;
            this.connectedAtMs = nowMs;
            this.windowStartMs = nowMs;
        }
    }

    /** Every connected session, keyed by socket address. */
    final ConcurrentHashMap<InetSocketAddress, SessionStats> allSessions              = new ConcurrentHashMap<>();
    /** Subset of {@link #allSessions} that are currently above the flood threshold. */
    private final ConcurrentHashMap<InetSocketAddress, SessionStats> floodingSessions = new ConcurrentHashMap<>();

    private final int          packetThreshold;     // packets/sec that flags a session as flooding
    private final int          minFloodingSessions; // minimum concurrent flooding sessions to evaluate
    private final int          minScore;            // minimum signal score to fire an alert
    private final LongSupplier clock;               // time source; injectable for testing

    /** Timestamp of the last signal evaluation. Updated regardless of whether an alert fired,
     *  so that evaluation is always throttled to once per {@link #ALERT_COOLDOWN_MS}. */
    private volatile long lastEvalMs = 0;

    public BotnetDetector(int packetThreshold, int minFloodingSessions, int minScore) {
        this(packetThreshold, minFloodingSessions, minScore, System::currentTimeMillis);
    }

    /** Package-private constructor for unit tests; allows injecting a controllable clock. */
    BotnetDetector(int packetThreshold, int minFloodingSessions, int minScore, LongSupplier clock) {
        this.packetThreshold     = packetThreshold;
        this.minFloodingSessions = minFloodingSessions;
        this.minScore            = minScore;
        this.clock               = clock;
    }

    public void registerSession(InetSocketAddress address) {
        allSessions.put(address, new SessionStats(address, clock.getAsLong()));
    }

    public void unregisterSession(InetSocketAddress address) {
        SessionStats removed = allSessions.remove(address);
        if (removed != null) {
            floodingSessions.remove(address);
        }
    }

    /**
     * Record one inbound packet for a session.
     * <p>
     * Called from the Netty I/O thread, must be fast and non-blocking.
     *
     * @param address  the session's full socket address (IP + port)
     * @param packetId the decoded packet type ID
     */
    public void recordPacket(InetSocketAddress address, int packetId) {
        SessionStats stats = allSessions.get(address);
        if (stats == null) return;

        long    now          = clock.getAsLong();
        boolean wasFlooding;
        boolean nowFlooding;
        int     capturedType;

        synchronized (stats) {
            wasFlooding = stats.flooding;

            if (now - stats.windowStartMs >= WINDOW_MS) {
                // The previous window just completed
                nowFlooding  = stats.windowCount >= packetThreshold;
                capturedType = stats.dominantTypeCandidate;

                stats.windowStartMs        = now;
                stats.windowCount          = 1;
                stats.dominantTypeCandidate = packetId;
                stats.dominantTypeScore    = 1;
            } else {
                // Still within the current window
                stats.windowCount++;

                // Boyer-Moore majority vote: O(1), no extra allocation
                if (packetId == stats.dominantTypeCandidate) {
                    stats.dominantTypeScore++;
                } else {
                    stats.dominantTypeScore--;
                    if (stats.dominantTypeScore <= 0) {
                        stats.dominantTypeCandidate = packetId;
                        stats.dominantTypeScore    = 1;
                    }
                }

                nowFlooding  = wasFlooding;
                capturedType = stats.dominantTypeCandidate;
            }
        }

        // Update volatile flood state outside the synchronized block
        if (!wasFlooding && nowFlooding) {
            stats.floodPacketType = capturedType;
            stats.floodOnsetMs    = now;
            stats.flooding        = true;
            floodingSessions.put(address, stats);
        } else if (wasFlooding && !nowFlooding) {
            stats.flooding = false;
            floodingSessions.remove(address);
        }
    }

    /**
     * Evaluate all signals once per server tick.
     * <p>
     * Returns a {@link BotnetReport} only when a botnet pattern is detected
     * AND the alert cooldown has elapsed. Returns {@link Optional#empty()} otherwise.
     */
    public Optional<BotnetReport> tick() {
        long now = clock.getAsLong();

        floodingSessions.entrySet().removeIf(entry -> {
            synchronized (entry.getValue()) {
                return now - entry.getValue().windowStartMs > WINDOW_MS * 2;
            }
        });

        List<SessionStats> flooding = new ArrayList<>(floodingSessions.values());
        if (flooding.size() < minFloodingSessions) {
            return Optional.empty();
        }

        if (now - lastEvalMs < ALERT_COOLDOWN_MS) {
            return Optional.empty();
        }
        lastEvalMs = now;

        EnumSet<BotnetReport.Signal> signals = EnumSet.noneOf(BotnetReport.Signal.class);
        int score = 0;

        // Signal 1: Volume: Always fires when we reach this point
        signals.add(BotnetReport.Signal.VOLUME);
        score++;

        // Signal 2: Timing: All flood onsets within TIMING_WINDOW_MS of each other
        long minOnset = flooding.stream().mapToLong(s -> s.floodOnsetMs).min().orElse(0L);
        long maxOnset = flooding.stream().mapToLong(s -> s.floodOnsetMs).max().orElse(0L);
        if (maxOnset - minOnset <= TIMING_WINDOW_MS) {
            signals.add(BotnetReport.Signal.TIMING);
            score++;
        }

        // Signal 3: Packet-type agreement: 70%+ share the same dominant packet type
        Map<Integer, Long> typeCounts = flooding.stream()
                .collect(Collectors.groupingBy(s -> s.floodPacketType, Collectors.counting()));
        long maxTypeCount = Collections.max(typeCounts.values());
        if ((double) maxTypeCount / flooding.size() >= PACKET_TYPE_AGREE) {
            signals.add(BotnetReport.Signal.PACKET_TYPE);
            score++;
        }

        // Signal 4: Subnet overlap: 50%+ from same /24 (or /48 for IPv6)
        Map<String, Long> subnetCounts = flooding.stream()
                .collect(Collectors.groupingBy(
                        s -> subnet24(s.address.getAddress()),
                        Collectors.counting()
                ));
        long maxSubnetCount = Collections.max(subnetCounts.values());
        if ((double) maxSubnetCount / flooding.size() >= SUBNET_OVERLAP) {
            signals.add(BotnetReport.Signal.SUBNET);
            score++;
        }

        // Signal 5: Fresh sessions: 70%+ connected within SESSION_FRESH_MS
        long freshCount = flooding.stream()
                .filter(s -> now - s.connectedAtMs <= SESSION_FRESH_MS)
                .count();
        if ((double) freshCount / flooding.size() >= FRESH_SESSION_RATIO) {
            signals.add(BotnetReport.Signal.FRESH_SESSIONS);
            score++;
        }

        if (score >= minScore) {
            Set<InetSocketAddress> addresses = flooding.stream()
                    .map(s -> s.address)
                    .collect(Collectors.toUnmodifiableSet());
            BotnetReport report = new BotnetReport(addresses, score, signals);
            log.warn("Botnet attack detected: {}", report);
            return Optional.of(report);
        }

        return Optional.empty();
    }

    public boolean isSuspicious(InetAddress ip) {
        return floodingSessions.keySet().stream()
                .anyMatch(addr -> addr.getAddress().equals(ip));
    }

    /**
     * Returns a string key representing the /24 subnet for IPv4,
     * or the /48 prefix for IPv6. Used to group sessions by network proximity.
     */
    private static String subnet24(InetAddress addr) {
        byte[] b = addr.getAddress();
        if (b.length == 4) {
            // IPv4: first three octets
            return (b[0] & 0xFF) + "." + (b[1] & 0xFF) + "." + (b[2] & 0xFF);
        }
        // IPv6: first 6 bytes (48-bit prefix)
        return String.format("%02x%02x:%02x%02x:%02x%02x", b[0], b[1], b[2], b[3], b[4], b[5]);
    }
}
