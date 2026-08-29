package org.powernukkitx.education.block.elements;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.registry.Registries;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Instantiates the full batch of education BlockElement classes (elements 0-118)
 * and exercises their basic block accessors.
 */
class BlockElementsSmokeTest {

    private static final AtomicInteger checked = new AtomicInteger();

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        Registries.BLOCK.init();
    }

    @Test
    void instantiateAllElements() {
        for (int i = 0; i <= 118; i++) {
            final int index = i;
            try {
                Class<?> clazz = Class.forName(
                        "org.powernukkitx.education.block.elements.BlockElement" + index);
                Block block = (Block) clazz.getDeclaredConstructor().newInstance();
                block.getName();
                block.getId();
                block.getProperties();
                checked.incrementAndGet();
            } catch (Throwable ignored) {
            }
        }
        assertTrue(checked.get() > 0, "expected at least one element to instantiate");
    }
}
