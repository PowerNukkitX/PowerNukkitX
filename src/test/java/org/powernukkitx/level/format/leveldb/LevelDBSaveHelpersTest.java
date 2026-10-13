package org.powernukkitx.level.format.leveldb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powernukkitx.level.format.IChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class LevelDBSaveHelpersTest {

    private static List<IChunk> chunks(int count) {
        List<IChunk> chunks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            chunks.add(Mockito.mock(IChunk.class));
        }
        return chunks;
    }

    @Test
    void serializesEveryChunkOnceWithHelpers() throws InterruptedException {
        List<IChunk> chunks = chunks(200);
        Set<IChunk> seen = ConcurrentHashMap.newKeySet();
        AtomicInteger calls = new AtomicInteger();
        Set<Thread> threads = ConcurrentHashMap.newKeySet();
        Thread caller = Thread.currentThread();
        CountDownLatch callerStarted = new CountDownLatch(1);
        ExecutorService helpers = Executors.newFixedThreadPool(4);
        try {
            LevelDBProvider.serializeWithHelpers(chunks, chunk -> {
                if (Thread.currentThread() == caller) {
                    callerStarted.countDown();
                } else {
                    try {
                        callerStarted.await(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                calls.incrementAndGet();
                seen.add(chunk);
                threads.add(Thread.currentThread());
            }, helpers);
        } finally {
            helpers.shutdown();
            helpers.awaitTermination(10, TimeUnit.SECONDS);
        }

        Assertions.assertEquals(200, calls.get());
        Assertions.assertEquals(200, seen.size());
        Assertions.assertTrue(threads.contains(Thread.currentThread()), "the caller takes part");
    }

    @Test
    void finishesOnTheCallerWhenHelpersNeverStart() {
        List<IChunk> chunks = chunks(64);
        AtomicInteger calls = new AtomicInteger();
        List<Runnable> neverRun = new ArrayList<>();

        LevelDBProvider.serializeWithHelpers(chunks, chunk -> calls.incrementAndGet(), neverRun::add);

        Assertions.assertEquals(64, calls.get());
        Assertions.assertFalse(neverRun.isEmpty());
        neverRun.forEach(Runnable::run);
        Assertions.assertEquals(64, calls.get());
    }

    @Test
    void queuesNoHelpersOnASaturatedPool() throws InterruptedException {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch busy = new CountDownLatch(1);
        try {
            pool.execute(() -> {
                busy.countDown();
                try {
                    release.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            Assertions.assertTrue(busy.await(10, TimeUnit.SECONDS));

            AtomicInteger calls = new AtomicInteger();
            LevelDBProvider.serializeWithHelpers(chunks(64), chunk -> calls.incrementAndGet(), pool);

            Assertions.assertEquals(64, calls.get());
            Assertions.assertTrue(pool.getQueue().isEmpty());
        } finally {
            release.countDown();
            pool.shutdown();
            pool.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    @Test
    void finishesWhenHelpersAreRefused() {
        AtomicInteger calls = new AtomicInteger();
        LevelDBProvider.serializeWithHelpers(chunks(32), chunk -> calls.incrementAndGet(), task -> {
            throw new RejectedExecutionException();
        });
        Assertions.assertEquals(32, calls.get());
    }

    @Test
    void rethrowsAFailureAfterTheRestIsSerialized() {
        List<IChunk> chunks = chunks(16);
        IChunk broken = chunks.get(3);
        AtomicInteger calls = new AtomicInteger();

        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class, () ->
            LevelDBProvider.serializeWithHelpers(chunks, chunk -> {
                calls.incrementAndGet();
                if (chunk == broken) {
                    throw new IllegalStateException("broken chunk");
                }
            }, null));

        Assertions.assertEquals("broken chunk", thrown.getMessage());
        Assertions.assertEquals(16, calls.get());
    }
}
