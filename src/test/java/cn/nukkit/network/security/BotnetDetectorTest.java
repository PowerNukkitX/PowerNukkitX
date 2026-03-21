package cn.nukkit.network.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BotnetDetector}.
 * <p>
 * Uses an injectable clock so no {@code Thread.sleep} is needed.
 * Detector config: threshold=5 pkt/s, minSessions=3, minScore=2.
 */
class BotnetDetectorTest {

    private static final int THRESHOLD    = 5;
    private static final int MIN_SESSIONS = 3;
    private static final int MIN_SCORE    = 2;

    private AtomicLong fakeTime;
    private BotnetDetector detector;

    @BeforeEach
    void setup() {
        fakeTime = new AtomicLong(1_000_000L);
        detector = new BotnetDetector(THRESHOLD, MIN_SESSIONS, MIN_SCORE, fakeTime::get);
    }

    @Test
    void normalTrafficDoesNotFlag() {
        InetSocketAddress a = addr("1.2.3.4", 1000);
        detector.registerSession(a);
        for (int i = 0; i < THRESHOLD - 1; i++) detector.recordPacket(a, 0x01);

        assertTrue(detector.tick().isEmpty());
    }

    @Test
    void sessionUnregisterClearsFloodingState() {
        InetSocketAddress a = addr("10.0.0.1", 1000);
        InetSocketAddress b = addr("10.0.0.2", 1001);
        InetSocketAddress c = addr("10.0.0.3", 1002);

        floodAll(0x01, a, b, c);
        detector.unregisterSession(a);
        detector.unregisterSession(b);
        detector.unregisterSession(c);

        assertTrue(detector.tick().isEmpty());
    }

    @Test
    void staleFloodingSessionsCleanedByTick() {
        InetSocketAddress a = addr("10.0.0.1", 1000);
        InetSocketAddress b = addr("10.0.0.2", 1001);
        InetSocketAddress c = addr("10.0.0.3", 1002);

        floodAll(0x01, a, b, c);

        // Advance 3s with no new packets so sessions exceed 2xWINDOW and are pruned
        fakeTime.addAndGet(3_000L);
        assertTrue(detector.tick().isEmpty());
    }

    @Test
    void evaluationThrottledDuringCooldown() {
        InetSocketAddress a = addr("10.0.0.1", 1000);
        InetSocketAddress b = addr("10.0.0.2", 1001);
        InetSocketAddress c = addr("10.0.0.3", 1002);

        floodAll(0x01, a, b, c);

        Optional<BotnetReport> first = detector.tick();
        assertTrue(first.isPresent(), "First evaluation should produce a report");

        Optional<BotnetReport> second = detector.tick();
        assertTrue(second.isEmpty(), "Second evaluation within cooldown must be suppressed");
    }

    @Test
    void subThresholdEvalAlsoThrottlesCooldown() {
        // minScore=6 is above the maximum possible score (5), so no report ever fires;
        // verify that the cooldown is still applied after a sub-threshold evaluation
        BotnetDetector strict = new BotnetDetector(THRESHOLD, MIN_SESSIONS, 6, fakeTime::get);

        floodAll(strict, 0x01,
                addr("10.0.0.1", 1000),
                addr("10.0.0.2", 1001),
                addr("10.0.0.3", 1002));

        assertTrue(strict.tick().isEmpty(), "minScore=6 is above max possible score, should never fire");
        assertTrue(strict.tick().isEmpty(), "Must be throttled even when sub-threshold fired previously");
    }

    @Test
    void fewerThanMinSessionsProducesNoReport() {
        floodAll(0x01, addr("10.0.0.1", 1000), addr("10.0.0.2", 1001));
        assertTrue(detector.tick().isEmpty());
    }

    @Test
    void volumeSignalAlwaysPresent() {
        floodAll(0x01,
                addr("10.0.0.1", 1000),
                addr("10.0.0.2", 1001),
                addr("10.0.0.3", 1002));

        BotnetReport report = detector.tick().orElseThrow();
        assertTrue(report.getSignals().contains(BotnetReport.Signal.VOLUME));
    }

    @Test
    void timingSignalPresentWhenOnsetsClose() {
        long base = fakeTime.get();
        floodAtTime(addr("10.0.0.1", 1000), 0x01, base);
        floodAtTime(addr("10.0.0.2", 1001), 0x01, base + 50);
        floodAtTime(addr("10.0.0.3", 1002), 0x01, base + 100);

        fakeTime.addAndGet(200L);
        BotnetReport report = detector.tick().orElseThrow();
        assertTrue(report.getSignals().contains(BotnetReport.Signal.TIMING));
    }

    @Test
    void timingSignalAbsentWhenOnsetsSpread() {
        long base = fakeTime.get();
        floodAtTime(addr("10.0.0.1", 1000), 0x01, base);
        floodAtTime(addr("10.0.0.2", 1001), 0x01, base + 3_000);
        floodAtTime(addr("10.0.0.3", 1002), 0x01, base + 6_000);

        fakeTime.addAndGet(200L);
        detector.tick().ifPresent(report ->
                assertFalse(report.getSignals().contains(BotnetReport.Signal.TIMING)));
    }

    @Test
    void packetTypeSignalPresentWhen70PercentMatch() {
        floodAll(0x10,
                addr("10.0.0.1", 1000),
                addr("10.0.0.2", 1001),
                addr("10.0.0.3", 1002));

        BotnetReport report = detector.tick().orElseThrow();
        assertTrue(report.getSignals().contains(BotnetReport.Signal.PACKET_TYPE));
    }

    @Test
    void packetTypeSignalAbsentWhenTypesDiffer() {
        // Each session floods with its own unique packet type
        floodAllMixed(
                new int[]{0x01, 0x02, 0x03},
                addr("10.0.0.1", 1000),
                addr("10.0.0.2", 1001),
                addr("10.0.0.3", 1002));

        detector.tick().ifPresent(report ->
                assertFalse(report.getSignals().contains(BotnetReport.Signal.PACKET_TYPE)));
    }

    @Test
    void subnetSignalPresentWhenSameSlash24() {
        floodAll(0x01,
                addr("192.168.1.1", 1000),
                addr("192.168.1.2", 1001),
                addr("192.168.1.3", 1002));

        BotnetReport report = detector.tick().orElseThrow();
        assertTrue(report.getSignals().contains(BotnetReport.Signal.SUBNET));
    }

    @Test
    void subnetSignalAbsentWhenDifferentSubnets() {
        floodAll(0x01,
                addr("10.0.0.1",    1000),
                addr("172.16.0.1",  1001),
                addr("192.168.0.1", 1002));

        detector.tick().ifPresent(report ->
                assertFalse(report.getSignals().contains(BotnetReport.Signal.SUBNET)));
    }

    @Test
    void freshSessionsSignalPresentWhenAllNew() {
        floodAll(0x01,
                addr("10.0.0.1", 1000),
                addr("10.0.0.2", 1001),
                addr("10.0.0.3", 1002));

        BotnetReport report = detector.tick().orElseThrow();
        assertTrue(report.getSignals().contains(BotnetReport.Signal.FRESH_SESSIONS));
    }

    @Test
    void freshSessionsSignalAbsentWhenSessionsOld() {
        InetSocketAddress a = addr("10.0.0.1", 1000);
        InetSocketAddress b = addr("10.0.0.2", 1001);
        InetSocketAddress c = addr("10.0.0.3", 1002);

        // Register sessions now, then age them past 30s before flooding
        detector.registerSession(a);
        detector.registerSession(b);
        detector.registerSession(c);
        fakeTime.addAndGet(40_000L);

        // Flood without re-registering (preserves original connectedAtMs)
        floodAllPreRegistered(0x01, a, b, c);

        BotnetReport report = detector.tick().orElseThrow();
        assertFalse(report.getSignals().contains(BotnetReport.Signal.FRESH_SESSIONS));
    }

    @Test
    void reportContainsFloodingAddresses() {
        InetSocketAddress a = addr("10.0.0.1", 1000);
        InetSocketAddress b = addr("10.0.0.2", 1001);
        InetSocketAddress c = addr("10.0.0.3", 1002);

        floodAll(0x01, a, b, c);

        BotnetReport report = detector.tick().orElseThrow();
        assertEquals(3, report.getFloodingSessions().size());
        assertTrue(report.getFloodingAddresses().stream()
                .allMatch(ip -> ip.getHostAddress().startsWith("10.0.0.")));
    }

    @Test
    void scoreReflectsNumberOfSignalsFired() {
        long base = fakeTime.get();
        floodAtTime(addr("192.168.1.1", 1000), 0x10, base);
        floodAtTime(addr("192.168.1.2", 1001), 0x10, base + 50);
        floodAtTime(addr("192.168.1.3", 1002), 0x10, base + 100);

        fakeTime.addAndGet(200L);
        BotnetReport report = detector.tick().orElseThrow();
        assertEquals(report.getScore(), report.getSignals().size());
        assertTrue(report.getScore() >= 4, "Should score at least 4 with all signals aligned");
    }

    @Test
    void boyerMooreCandidateCorrectForMajority() {
        InetSocketAddress a = addr("10.0.0.1", 1000);
        detector.registerSession(a);

        // 8x 0xAA, 3x 0xBB - 0xAA is the majority
        for (int i = 0; i < 8; i++) detector.recordPacket(a, 0xAA);
        for (int i = 0; i < 3; i++) detector.recordPacket(a, 0xBB);

        fakeTime.addAndGet(1_100L);
        detector.recordPacket(a, 0xAA); // trigger rollover; new window starts with 0xAA

        BotnetDetector.SessionStats stats = detector.allSessions.get(a);
        assertNotNull(stats);
        assertEquals(0xAA, stats.dominantTypeCandidate);
    }

    @Test
    void isSuspiciousReturnsTrueForFloodingIp() {
        InetSocketAddress a = addr("10.0.0.1", 1000);
        floodAll(0x01, a);
        assertTrue(detector.isSuspicious(a.getAddress()));
    }

    @Test
    void isSuspiciousReturnsFalseForCleanIp() {
        InetSocketAddress a = addr("10.0.0.99", 9999);
        detector.registerSession(a);
        detector.recordPacket(a, 0x01); // well below threshold
        assertFalse(detector.isSuspicious(a.getAddress()));
    }

    private InetSocketAddress addr(String ip, int port) {
        return new InetSocketAddress(ip, port);
    }

    /**
     * Register and flood all {@code addrs} simultaneously in {@code det}.
     * All sessions share the same registration time and window rollover moment,
     * so no session goes stale before tick() is called.
     */
    private void floodAll(BotnetDetector det, int packetId, InetSocketAddress... addrs) {
        long start = fakeTime.get();
        for (var addr : addrs) det.registerSession(addr);

        // Send THRESHOLD+1 packets to each session interleaved, all stay within the 1s window
        for (int i = 0; i < THRESHOLD + 1; i++) {
            for (var addr : addrs) det.recordPacket(addr, packetId);
            fakeTime.addAndGet(1L);
        }

        // Jump to just past the 1s window and send a trigger packet to each (causes rollover + flooding flag)
        fakeTime.set(start + 1_100L);
        for (var addr : addrs) {
            det.recordPacket(addr, packetId);
            fakeTime.addAndGet(1L);
        }
    }

    /** Shorthand using the default {@link #detector}. */
    private void floodAll(int packetId, InetSocketAddress... addrs) {
        floodAll(detector, packetId, addrs);
    }

    /**
     * Flood pre-registered sessions in the default detector without calling registerSession.
     * Use this when connectedAtMs must reflect an earlier registration time.
     */
    private void floodAllPreRegistered(int packetId, InetSocketAddress... addrs) {
        long start = fakeTime.get();
        for (int i = 0; i < THRESHOLD + 1; i++) {
            for (var addr : addrs) detector.recordPacket(addr, packetId);
            fakeTime.addAndGet(1L);
        }
        fakeTime.set(start + 1_100L);
        for (var addr : addrs) {
            detector.recordPacket(addr, packetId);
            fakeTime.addAndGet(1L);
        }
    }

    /**
     * Flood sessions simultaneously where each session uses a different packet type.
     * {@code packetIds[i]} is the type for {@code addrs[i]}.
     */
    private void floodAllMixed(int[] packetIds, InetSocketAddress... addrs) {
        long start = fakeTime.get();
        for (var addr : addrs) detector.registerSession(addr);
        for (int i = 0; i < THRESHOLD + 1; i++) {
            for (int j = 0; j < addrs.length; j++) detector.recordPacket(addrs[j], packetIds[j]);
            fakeTime.addAndGet(1L);
        }
        fakeTime.set(start + 1_100L);
        for (int j = 0; j < addrs.length; j++) {
            detector.recordPacket(addrs[j], packetIds[j]);
            fakeTime.addAndGet(1L);
        }
    }

    /**
     * Register and flood a single session, pinning the window start to {@code onsetMs}.
     * Used for timing-signal tests where different sessions must have measurably different onset times.
     */
    private void floodAtTime(InetSocketAddress addr, int packetId, long onsetMs) {
        fakeTime.set(onsetMs);
        detector.registerSession(addr);
        for (int i = 0; i < THRESHOLD + 1; i++) {
            detector.recordPacket(addr, packetId);
            fakeTime.addAndGet(1L);
        }
        fakeTime.set(onsetMs + 1_100L);
        detector.recordPacket(addr, packetId);
    }
}
