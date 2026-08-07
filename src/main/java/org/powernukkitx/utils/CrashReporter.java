package org.powernukkitx.utils;

import org.powernukkitx.Player;
import org.powernukkitx.event.player.PlayerKickEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralizes the logging and rate-limiting of internal server errors.
 *
 * Each "distinct" error prints its full stack trace once, then further
 * occurrences are counted and summarized periodically so logs don't get
 * flooded.
 *
 * Thread-safe: can be called from any server thread.
 */
public final class CrashReporter {

    /** Minimum delay between two summaries of the same recurring error. */
    public static final long SUMMARY_DELAY_MS = 60_000L;

    private static final Logger LOGGER = Logger.getLogger(CrashReporter.class.getName());

    private static final ConcurrentMap<String, Occurrence> HISTORY = new ConcurrentHashMap<>();

    private CrashReporter() {
        throw new UnsupportedOperationException("utility class, cannot be instantiated");
    }

    /** State kept for a given error signature. */
    private static final class Occurrence {
        final AtomicLong countSinceReport = new AtomicLong();
        volatile long lastReportedAt;
        volatile boolean reportedOnce;
    }

    /**
     * Logs a caught and isolated error.
     *
     * @param task    what the server was doing when the error occurred, phrased so
     *                the log line is actionable on its own, e.g. "ticking a player",
     *                "sending an entity to the player"
     * @param culprit the object responsible for the error (player, entity, packet...).
     *                Its toString() is only evaluated if the error is actually printed.
     *                Can be null.
     * @param error   the caught exception
     */
    public static void log(String task, Object culprit, Throwable error) {
        if (task == null || error == null) {
            throw new IllegalArgumentException("task and error cannot be null");
        }

        final String signature = buildSignature(task, error);
        final Occurrence occurrence = HISTORY.computeIfAbsent(signature, unused -> new Occurrence());

        final long timesSkipped = occurrence.countSinceReport.getAndIncrement();
        final long now = System.currentTimeMillis();

        final boolean withinCooldown = occurrence.reportedOnce
            && (now - occurrence.lastReportedAt) < SUMMARY_DELAY_MS;
        if (withinCooldown) {
            return;
        }

        occurrence.reportedOnce = true;
        occurrence.lastReportedAt = now;
        occurrence.countSinceReport.set(0);

        if (timesSkipped == 0) {
            LOGGER.log(Level.SEVERE, formatHeader(task, culprit), error);
        } else {
            LOGGER.log(Level.SEVERE, formatHeader(task, culprit) + " - happened " + timesSkipped
                + " more time(s) since the last report", error);
        }
    }

    /**
     * Logs an error then disconnects only the player it is attributed to,
     * leaving everyone else connected. The disconnect screen shows the
     * exception type and message.
     *
     * Only use this when the error came directly from that player's own tick
     * or session handling. For an error tied to shared state (a broken entity
     * or chunk), skip the offending object instead of disconnecting, otherwise
     * every player who comes near it gets kicked in turn.
     *
     * @param player player to disconnect
     * @param task   what the server was doing, see {@link #log(String, Object, Throwable)}
     * @param error  the caught exception
     */
    public static void logAndDisconnect(Player player, String task, Throwable error) {
        if (player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }

        log(task, player, error);

        final String reason = "Internal error while " + task + " - " + summarize(error);
        try {
            player.kick(PlayerKickEvent.Reason.UNKNOWN, reason,
                "Internal error while " + task + "\n" + summarize(error), false);
        } catch (Throwable kickError) {
            log("disconnecting a player after an internal error", player, kickError);
        }
    }

    public static String summarize(Throwable error) {
        final String message = error.getMessage();
        final String type = error.getClass().getSimpleName();
        return message == null ? type : type + ": " + message;
    }

    private static String buildSignature(String task, Throwable error) {
        final StackTraceElement[] trace = error.getStackTrace();
        final String origin = trace.length == 0 ? "unknown" : trace[0].toString();
        return task + "::" + error.getClass().getName() + "::" + origin;
    }

    private static String formatHeader(String task, Object culprit) {
        return "Isolated error while " + task + " [" + describe(culprit) + "]";
    }

    private static String describe(Object culprit) {
        if (culprit == null) {
            return "no subject";
        }
        try {
            return culprit.getClass().getSimpleName() + ": " + culprit;
        } catch (Throwable toStringFailed) {
            return culprit.getClass().getSimpleName() + ": <toString failed>";
        }
    }
}
