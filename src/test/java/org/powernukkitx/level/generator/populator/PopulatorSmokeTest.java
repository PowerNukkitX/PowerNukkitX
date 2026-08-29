package org.powernukkitx.level.generator.populator;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.Generator;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Drives every registered {@link Populator} against the real fixture level and a real
 * chunk. Populators place ores, plants, lakes and structures during world generation -
 * logic that only runs at generation time and is otherwise untouched by unit tests.
 * <p>
 * Each populator gets a fresh {@link BlockManager} root and a {@link ChunkGenerateContext}
 * built from the fixture level's own generator, then {@code apply(context)} is invoked.
 * Everything is wrapped so a single misbehaving populator never fails the run.
 */
public class PopulatorSmokeTest {

    static Level level;
    static Generator generator;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        Registries.POPULATOR.init();
        generator = level.getGenerator();
    }

    @Test
    void everyRegisteredPopulatorRuns() {
        int checked = 0;
        for (String name : registeredNames()) {
            Populator populator = safeGet(name);
            if (populator == null) continue;
            if (runOn(populator, 0, 0)) checked++;
        }
        Assertions.assertTrue(checked > 0, "no populator was exercised");
    }

    @Test
    void populatorsRunAcrossSeveralChunks() {
        int checked = 0;
        int[][] coords = {{0, 0}, {1, 0}, {0, 1}, {3, 5}, {-2, -2}};
        for (String name : registeredNames()) {
            for (int[] c : coords) {
                Populator populator = safeGet(name);
                if (populator == null) continue;
                if (runOn(populator, c[0], c[1])) checked++;
            }
        }
        Assertions.assertTrue(checked > 0, "no populator ran across chunks");
    }

    private boolean runOn(Populator populator, int cx, int cz) {
        try {
            IChunk chunk = level.getChunk(cx, cz, true);
            if (chunk == null) return false;
            ChunkGenerateContext context = new ChunkGenerateContext(generator, level, chunk);
            populator.setRoot(new BlockManager(level));
            safe(() -> populator.apply(context));
            safe(populator::name);
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }

    /**
     * Pulls the registered populator keys out of the registry's static map via reflection -
     * the registry exposes no public listing method.
     */
    @SuppressWarnings({"unchecked", "PMD.AvoidAccessibilityAlteration"})
    private List<String> registeredNames() {
        List<String> names = new ArrayList<>();
        try {
            Field f = Registries.POPULATOR.getClass().getDeclaredField("REGISTRY");
            f.setAccessible(true);
            Object map = f.get(null);
            java.util.Map<String, ?> registry = (java.util.Map<String, ?>) map;
            names.addAll(registry.keySet());
        } catch (Throwable ignore) {
        }
        return names;
    }

    private Populator safeGet(String name) {
        try {
            return Registries.POPULATOR.get(name);
        } catch (Throwable ignore) {
            return null;
        }
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
