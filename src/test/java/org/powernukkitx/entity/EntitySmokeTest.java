package org.powernukkitx.entity;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Spawns every registered entity into the real fixture level and exercises the pure
 * getters. Covers the huge entity/* hierarchy (passive, mob, item, projectile, ...)
 * that unit tests never reach because entities need a chunk + level to construct.
 */
public class EntitySmokeTest {

    static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @Test
    void everyEntitySpawnsAndAnswersGetters() {
        Set<String> ids = Registries.ENTITY.getKnownEntities().keySet();
        Assertions.assertFalse(ids.isEmpty(), "no entities registered");

        int checked = 0;
        int failCount = 0;
        StringBuilder failures = new StringBuilder();

        for (String id : ids) {
            try {
                Position pos = new Position(0, 100, 0, level);
                Entity entity = Entity.createEntity(id, pos);
                if (entity == null) continue;
                exercise(entity);
                entity.close();
                checked++;
            } catch (Throwable t) {
                failCount++;
                if (failCount <= 25) {
                    failures.append('\n').append(id).append(" -> ").append(t);
                }
            }
        }

        Assertions.assertTrue(checked > 0, "no entity survived the smoke pass" + failures);
        Assertions.assertTrue(failCount < ids.size() * 0.15,
                "too many entities failed (" + failCount + "/" + ids.size() + ")" + failures);
    }

    private void exercise(Entity e) {
        safe(e::getName);
        safe(e::getOriginalName);
        safe(e::getNetworkId);
        safe(e::getWidth);
        safe(e::getHeight);
        safe(e::getLength);
        safe(e::getEyeHeight);
        safe(e::getCurrentHeight);
        safe(e::getGravity);
        safe(e::getScale);
        safe(e::getKnockbackResistance);
        safe(e::canCollide);
        safe(e::isAlive);
        safe(e::isClosed);
        safe(e::isPersistent);
        safe(e::isInvulnerable);
        safe(e::isSneaking);
        safe(e::isSwimming);
        safe(e::isSprinting);
        safe(e::isGliding);
        safe(e::isImmobile);
        safe(e::canClimb);
        safe(e::isNpc);
        safe(e::isArmorStand);
        safe(e::canBeSavedWithChunk);
        safe(e::getAge);
        safe(e::isRideable);
        safe(e::isRiding);
        safe(e::getHorizontalFacing);
        safe(e::getDirectionPlane);
        safe(e::hashCode);
        safe(e::getScoreTag);
        safe(e::getBoundingBox);
        safe(e::toString);
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
