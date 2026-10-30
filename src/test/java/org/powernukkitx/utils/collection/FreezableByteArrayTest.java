
package org.powernukkitx.utils.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Regression tests for concurrent freezing and thawing.
 *
 * @author Curse
 */
class FreezableByteArrayTest {

    private FreezableArrayManager manager() {
        return new FreezableArrayManager(true, 32, -256, 0, -256, 1024, 16, 1, 32);
    }

    private byte[] source() {
        byte[] result = new byte[2048];
        for (int i = 0; i < result.length; i++) result[i] = (byte) (i * 31 + 7);
        return result;
    }

    @Test
    void freezeAndDeepFreezePreserveContent() {
        byte[] expected = source();
        FreezableByteArray array = new FreezableByteArray(expected.clone(), manager());

        array.freeze();
        Assertions.assertEquals(AutoFreezable.FreezeStatus.FREEZE, array.getFreezeStatus());
        Assertions.assertArrayEquals(expected, array.getRawBytes());
        Assertions.assertEquals(AutoFreezable.FreezeStatus.NONE, array.getFreezeStatus());

        array.colder(1024);
        array.freeze();
        array.deepFreeze();
        Assertions.assertEquals(AutoFreezable.FreezeStatus.DEEP_FREEZE, array.getFreezeStatus());
        Assertions.assertArrayEquals(expected, array.getRawBytes());
    }

    @Test
    void concurrentThawPreservesContent() throws Exception {
        final int workers = 12;
        final int rounds = 96;
        byte[] expected = source();
        FreezableByteArray array = new FreezableByteArray(expected.clone(), manager());
        var executor = Executors.newFixedThreadPool(workers);

        try {
            for (int round = 0; round < rounds; round++) {
                array.colder(1024);
                array.freeze();
                Assertions.assertEquals(AutoFreezable.FreezeStatus.FREEZE, array.getFreezeStatus());

                var start = new CountDownLatch(1);
                List<Future<?>> futures = new ArrayList<>(workers);

                for (int worker = 0; worker < workers; worker++) {
                    final int index = worker * 127 % expected.length;
                    futures.add(executor.submit(() -> {
                        start.await();
                        Assertions.assertEquals(expected[index], array.getByte(index));
                        Assertions.assertArrayEquals(expected, array.getRawBytes());
                        return null;
                    }));
                }

                start.countDown();
                for (Future<?> future : futures) future.get(30, TimeUnit.SECONDS);

                Assertions.assertEquals(AutoFreezable.FreezeStatus.NONE, array.getFreezeStatus());
            }
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void concurrentFreezeAndWritesPreserveContent() throws Exception {
        final int workers = 4;
        final int partitionSize = 512;
        FreezableByteArray array = new FreezableByteArray(new byte[2048], manager());
        byte[] expected = new byte[2048];

        for (int i = 0; i < expected.length; i++) expected[i] = (byte) (i ^ 0x5a);

        var executor = Executors.newFixedThreadPool(workers + 1);
        try {
            var start = new CountDownLatch(1);
            List<Future<?>> futures = new ArrayList<>();

            for (int worker = 0; worker < workers; worker++) {
                final int begin = worker * partitionSize;
                futures.add(executor.submit(() -> {
                    start.await();
                    for (int i = begin; i < begin + partitionSize; i++) {
                        array.setByte(i, expected[i]);
                    }
                    return null;
                }));
            }

            futures.add(executor.submit(() -> {
                start.await();
                for (int i = 0; i < 256; i++) {
                    array.colder(1024);
                    if ((i & 1) == 0) array.freeze();
                    else array.deepFreeze();
                    array.thaw();
                }
                return null;
            }));

            start.countDown();
            for (Future<?> future : futures) future.get(30, TimeUnit.SECONDS);

            Assertions.assertArrayEquals(expected, array.getRawBytes());
            Assertions.assertEquals(AutoFreezable.FreezeStatus.NONE, array.getFreezeStatus());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void replacingFrozenDataDoesNotCorruptArray() {
        FreezableByteArray array = new FreezableByteArray(source(), manager());
        byte[] replacement = new byte[2048];

        for (int i = 0; i < replacement.length; i++) replacement[i] = (byte) (i ^ 0x35);

        array.freeze();
        array.setRawBytes(replacement);

        Assertions.assertEquals(AutoFreezable.FreezeStatus.NONE, array.getFreezeStatus());
        Assertions.assertArrayEquals(replacement, array.getRawBytes());

        array.colder(1024);
        array.freeze();
        Assertions.assertArrayEquals(replacement, array.getRawBytes());
    }
}
