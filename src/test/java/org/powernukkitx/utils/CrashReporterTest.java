package org.powernukkitx.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The rate-limiting logic is the part of {@link CrashReporter} that's easy to get backwards: an
 * error repeating every tick must be printed once, not twenty times a second, and a distinct error
 * must never get hidden behind an unrelated one that's already being throttled.
 */
class CrashReporterTest {

    private CapturingHandler handler;
    private Logger logger;

    private static final class CapturingHandler extends Handler {
        private final List<LogRecord> records = new CopyOnWriteArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }

    @BeforeEach
    void attachHandler() {
        handler = new CapturingHandler();
        logger = Logger.getLogger(CrashReporter.class.getName());
        logger.addHandler(handler);
    }

    @AfterEach
    void detachHandler() {
        logger.removeHandler(handler);
    }

    @Test
    void printsTheFirstOccurrenceWithItsStackTrace() {
        final IllegalStateException thrown = new IllegalStateException("boom");
        CrashReporter.log(uniqueTask(), "subject", thrown);

        assertEquals(1, handler.records.size());
        final LogRecord record = handler.records.getFirst();
        assertEquals(Level.SEVERE, record.getLevel());
        assertNotNull(record.getThrown(), "The throwable must reach the log, not only its message");
        assertTrue(record.getMessage().contains("subject"));
    }

    @Test
    void throttlesTheSameErrorRepeatingEveryTick() {
        final String task = uniqueTask();
        for (int tick = 0; tick < 100; tick++) {
            CrashReporter.log(task, "subject", new IllegalStateException("boom"));
        }
        assertEquals(1, handler.records.size(), "An error repeating every tick must be printed once");
    }

    @Test
    void doesNotHideAnUnrelatedErrorBehindAThrottledOne() {
        final String throttledTask = uniqueTask();
        for (int tick = 0; tick < 10; tick++) {
            CrashReporter.log(throttledTask, "subject", new IllegalStateException("boom"));
        }
        CrashReporter.log(uniqueTask(), "other subject", new IllegalStateException("boom"));

        assertEquals(2, handler.records.size());
    }

    @Test
    void survivesASubjectWhoseToStringThrows() {
        final Object hostile = new Object() {
            @Override
            public String toString() {
                throw new UnsupportedOperationException();
            }
        };
        CrashReporter.log(uniqueTask(), hostile, new IllegalStateException("boom"));

        assertEquals(1, handler.records.size());
    }

    /**
     * Errors are throttled per task for the lifetime of the JVM, so every test needs its own
     * unique task description.
     */
    private static String uniqueTask() {
        return "a unit test " + System.nanoTime();
    }
}
