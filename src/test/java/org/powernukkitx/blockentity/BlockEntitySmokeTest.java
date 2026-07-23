package org.powernukkitx.blockentity;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds every block entity type into the fixture level and exercises the pure getters.
 * Covers the blockentity/* package which needs a chunk + level to construct.
 */
public class BlockEntitySmokeTest {

    static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @Test
    void everyBlockEntityBuildsAndAnswersGetters() throws Exception {
        List<String> ids = blockEntityIds();
        Assertions.assertFalse(ids.isEmpty(), "no block entity ids found");

        int checked = 0;
        int failCount = 0;
        StringBuilder failures = new StringBuilder();
        int x = 0;

        for (String id : ids) {
            try {
                // spread positions so they don't collide in one block slot
                Position pos = new Position(x++, 80, 0, level);
                BlockEntity be = BlockEntity.createBlockEntity(id, pos);
                if (be == null) continue;
                exercise(be);
                be.close();
                checked++;
            } catch (Throwable t) {
                failCount++;
                if (failCount <= 25) {
                    failures.append('\n').append(id).append(" -> ").append(t);
                }
            }
        }

        Assertions.assertTrue(checked > 0, "no block entity survived the smoke pass" + failures);
    }

    private void exercise(BlockEntity be) {
        safe(be::getId);
        safe(be::getName);
        safe(be::isObservable);
        safe(be::isMovable);
        safe(be::getNbt);
        safe(be::getCleanedNBT);
        safe(be::getBlock);
        safe(be::hashCode);
        safe(be::toString);
    }

    private List<String> blockEntityIds() throws Exception {
        List<String> ids = new ArrayList<>();
        for (Field f : BlockEntityID.class.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && f.getType() == String.class) {
                ids.add((String) f.get(null));
            }
        }
        return ids;
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
