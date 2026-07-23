package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MinecraftNamespaceComparatorTest {
    @Test
    void comparesByChildDescending() {
        // child compared as idB.child.compareTo(idA.child): "apple" vs "banana"
        int r = MinecraftNamespaceComparator.compare("minecraft:apple", "minecraft:banana");
        Assertions.assertEquals("banana".compareTo("apple"), r);
        Assertions.assertTrue(r > 0);
    }

    @Test
    void equalChildFallsBackToNamespace() {
        int r = MinecraftNamespaceComparator.compare("aaa:stone", "bbb:stone");
        // namespaceB.compareTo(namespaceA): "bbb".compareTo("aaa") > 0
        Assertions.assertEquals("bbb".compareTo("aaa"), r);
    }

    @Test
    void fullyEqualIsZero() {
        Assertions.assertEquals(0, MinecraftNamespaceComparator.compare("minecraft:stone", "minecraft:stone"));
    }

    @Test
    void compareFNVConsistentAndSymmetric() {
        int r1 = MinecraftNamespaceComparator.compareFNV("minecraft:stone", "minecraft:dirt");
        int r2 = MinecraftNamespaceComparator.compareFNV("minecraft:dirt", "minecraft:stone");
        Assertions.assertEquals(Integer.signum(r1), -Integer.signum(r2));
        Assertions.assertEquals(0, MinecraftNamespaceComparator.compareFNV("a:b", "a:b"));
    }
}
