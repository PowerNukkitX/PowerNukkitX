package org.powernukkitx.utils.collection;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class FreezableBucketTest {

    private static final java.util.function.Predicate<AutoFreezable> ALL = e -> true;

    private FreezableByteArray array(FreezableArrayManager manager) {
        return new FreezableByteArray(1, manager);
    }

    private FreezableArrayManager manager() {
        return new FreezableArrayManager(true, 32, 32, 0, -256, 1024, 16, 1, 32);
    }

    @Test
    void scanIsBoundedByMaxScan() {
        var m = manager();
        var bucket = new FreezableBucket();
        List<FreezableByteArray> strong = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var e = array(m);
            strong.add(e);
            bucket.add(e);
        }

        var out = new ArrayList<AutoFreezable>();
        assertEquals(4, bucket.scan(4, ALL, out));
        assertEquals(strong.subList(0, 4), out);
        assertEquals(10, bucket.size());
    }

    @Test
    void cursorResumesWhereThePreviousScanStopped() {
        var m = manager();
        var bucket = new FreezableBucket();
        List<FreezableByteArray> strong = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            var e = array(m);
            strong.add(e);
            bucket.add(e);
        }

        var first = new ArrayList<AutoFreezable>();
        bucket.scan(4, ALL, first);
        var second = new ArrayList<AutoFreezable>();
        bucket.scan(4, ALL, second);

        // Four then four over six entries: the second pass must reach the two the first missed and
        // then wrap, rather than starting from the front again.
        assertEquals(List.of(strong.get(4), strong.get(5)), second.subList(0, 2));
        assertTrue(first.containsAll(strong.subList(0, 4)));
    }

    @Test
    void onlyCandidatesAreCollected() {
        var m = manager();
        var bucket = new FreezableBucket();
        List<FreezableByteArray> strong = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            var e = array(m);
            strong.add(e);
            bucket.add(e);
        }
        var wanted = strong.get(2);

        var out = new ArrayList<AutoFreezable>();
        assertEquals(4, bucket.scan(16, e -> e == wanted, out));
        assertEquals(List.of(wanted), out);
    }

    @Test
    void collectedEntriesAreDroppedAndTheBucketShrinks() {
        var m = manager();
        var bucket = new FreezableBucket();
        // Only the first entry stays reachable; the rest are dropped on the floor.
        var kept = array(m);
        bucket.add(kept);
        for (int i = 0; i < 200; i++) {
            bucket.add(array(m));
        }
        assertEquals(201, bucket.size());

        var out = new ArrayList<AutoFreezable>();
        for (int attempt = 0; attempt < 10 && bucket.size() == 201; attempt++) {
            System.gc();
            out.clear();
            bucket.scan(Integer.MAX_VALUE, ALL, out);
        }
        assumeTrue(bucket.size() < 201, "the GC never cleared the unreachable entries");

        assertTrue(out.contains(kept), "a live entry must survive the sweep");
    }
}
