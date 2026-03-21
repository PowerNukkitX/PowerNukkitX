package cn.nukkit.network.security;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Snapshot of a detected botnet attack, produced by {@link BotnetDetector#tick()}.
 * <p>
 * Each report carries a {@link #getScore() score} (1–5) and the set of
 * {@link Signal corroborating signals} that fired. A higher score means stronger
 * evidence that the flooding is coordinated rather than coincidental.
 */
public final class BotnetReport {

    /**
     * Corroborating signals used to distinguish a real botnet from independent
     * high-traffic sessions or benign NAT clusters.
     */
    public enum Signal {
        /**
         * N+ sessions are simultaneously flooding above the packet-rate threshold.
         * This is the baseline signal which is always present in every report.
         */
        VOLUME,

        /**
         * All flood onset times are within 2 seconds of each other.
         * Indicates a coordinated start (e.g. a single script launching threads).
         */
        TIMING,

        /**
         * 70%+ of flooding sessions share the same dominant packet type.
         * Indicates all attackers are running the same exploit script.
         */
        PACKET_TYPE,

        /**
         * 50%+ of flooding sessions belong to the same /24 subnet.
         * Typical of VPS botnets where adjacent IPs are rented from the same block.
         */
        SUBNET,

        /**
         * 70%+ of flooding sessions connected less than 30 seconds ago.
         * Fresh connections that immediately start flooding are characteristic of bots.
         */
        FRESH_SESSIONS
    }

    private final Set<InetSocketAddress> floodingSessions;
    private final int score;
    private final EnumSet<Signal> signals;

    BotnetReport(Set<InetSocketAddress> floodingSessions, int score, EnumSet<Signal> signals) {
        this.floodingSessions = floodingSessions;
        this.score = score;
        this.signals = signals;
    }

    /** The socket addresses of every session that was flooding when this report was generated. */
    public Set<InetSocketAddress> getFloodingSessions() {
        return floodingSessions;
    }

    /** Unique IP addresses of all flooding sessions (port stripped, deduplicated). */
    public Set<InetAddress> getFloodingAddresses() {
        return floodingSessions.stream()
                .map(InetSocketAddress::getAddress)
                .collect(Collectors.toUnmodifiableSet());
    }

    /** Total number of corroborating signals that fired. Range: 1–5. */
    public int getScore() {
        return score;
    }

    /** The signals that fired. Always contains at least {@link Signal#VOLUME}. */
    public EnumSet<Signal> getSignals() {
        return signals.clone();
    }

    @Override
    public String toString() {
        return "BotnetReport{sessions=" + floodingSessions.size()
                + ", score=" + score + "/5"
                + ", signals=" + signals + "}";
    }
}
