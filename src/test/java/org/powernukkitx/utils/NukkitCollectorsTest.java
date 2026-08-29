package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

public class NukkitCollectorsTest {
    @Test
    void countingIntCounts() {
        int count = Stream.of("a", "b", "c", "d").collect(NukkitCollectors.countingInt());
        Assertions.assertEquals(4, count);
    }

    @Test
    void countingIntEmptyIsZero() {
        int count = List.<String>of().stream().collect(NukkitCollectors.countingInt());
        Assertions.assertEquals(0, count);
    }
}
