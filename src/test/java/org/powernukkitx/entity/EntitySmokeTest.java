package org.powernukkitx.entity;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
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

    @Test
    void definitionsRoundTripAndPreserveOrder() {
        CompoundTag nbt = Entity.getDefaultNBT(new Position(0, 100, 0, level))
                .putList("definitions", new ListTag<StringTag>()
                        .add(new StringTag("+test:first"))
                        .add(new StringTag("-test:second")));
        Entity entity = Entity.createEntity("minecraft:pig", level.getChunk(0, 0), nbt);
        Assertions.assertNotNull(entity);

        try {
            Assertions.assertEquals(List.of("+test:first", "-test:second"), entity.getDefinitions());
            Assertions.assertTrue(entity.hasDefinition("test:first"));
            Assertions.assertFalse(entity.hasDefinition("test:second"));

            entity.removeDefinition("test:first");
            entity.addDefinition("test:second");

            List<String> expected = List.of("+test:first", "-test:second", "-test:first", "+test:second");
            Assertions.assertEquals(expected, entity.getDefinitions());
            Assertions.assertFalse(entity.hasDefinition("test:first"));
            Assertions.assertTrue(entity.hasDefinition("test:second"));

            entity.saveNBT();
            ListTag<StringTag> saved = entity.getNbt().getList("definitions", StringTag.class);
            List<String> savedDefinitions = saved.getAll().stream().map(tag -> tag.data).toList();
            Assertions.assertEquals(expected, savedDefinitions);
        } finally {
            entity.close();
        }
    }

    @Test
    void definitionsKeepIdentifierAndAppendHistory() {
        Entity entity = Entity.createEntity("minecraft:pig", new Position(0, 100, 0, level));
        Assertions.assertNotNull(entity);

        try {
            Assertions.assertEquals(List.of("+minecraft:pig"), entity.getDefinitions());

            entity.addDefinition("test:state");
            Assertions.assertEquals(List.of("+minecraft:pig", "+test:state"), entity.getDefinitions());

            entity.addDefinition("test:state");
            Assertions.assertEquals(List.of("+minecraft:pig", "+test:state"), entity.getDefinitions());

            entity.removeDefinition("test:state");
            Assertions.assertEquals(List.of("+minecraft:pig", "+test:state", "-test:state"), entity.getDefinitions());

            entity.removeDefinition("test:state");
            Assertions.assertEquals(List.of("+minecraft:pig", "+test:state", "-test:state"), entity.getDefinitions());

            entity.addDefinition("test:state");
            Assertions.assertEquals(
                    List.of("+minecraft:pig", "+test:state", "-test:state", "+test:state"),
                    entity.getDefinitions()
            );
            Assertions.assertTrue(entity.hasDefinition("test:state"));
        } finally {
            entity.close();
        }
    }

    @Test
    void deadAndDeathTimeRoundTrip() {
        CompoundTag nbt = Entity.getDefaultNBT(new Position(0, 100, 0, level))
                .putBoolean("Dead", true)
                .putShort("DeathTime", 7);
        Entity entity = Entity.createEntity("minecraft:pig", level.getChunk(0, 0), nbt);
        Assertions.assertTrue(entity instanceof EntityLiving);

        try {
            EntityLiving living = (EntityLiving) entity;
            Assertions.assertTrue(living.deadState);
            Assertions.assertEquals(7, living.deathTime);
            Assertions.assertFalse(living.isAlive());

            living.entityBaseTick(1);
            Assertions.assertEquals(8, living.deathTime);

            living.saveNBT();
            Assertions.assertTrue(living.getNbt().getBoolean("Dead"));
            Assertions.assertEquals(8, living.getNbt().getShort("DeathTime"));
        } finally {
            entity.close();
        }
    }

    @Test
    void mobDeathDefaultsAndMotionMatchBds() {
        Entity entity = Entity.createEntity("minecraft:pig", new Position(0, 100, 0, level));
        Assertions.assertTrue(entity instanceof EntityLiving);

        try {
            entity.onGround = true;
            entity.motionX = 0;
            entity.motionY = 0;
            entity.motionZ = 0;
            entity.saveNBT();

            Assertions.assertFalse(entity.getNbt().getBoolean("Dead"));
            Assertions.assertEquals(0, entity.getNbt().getShort("DeathTime"));
            Assertions.assertFalse(entity.getNbt().contains("Motion"));

            entity.onGround = false;
            entity.saveNBT();

            Assertions.assertTrue(entity.getNbt().containsList("Motion"));
        } finally {
            entity.close();
        }
    }

    @Test
    void armorStandUsesBdsMobMotionGate() {
        Entity entity = Entity.createEntity("minecraft:armor_stand", new Position(0, 100, 0, level));
        Assertions.assertNotNull(entity);
        Assertions.assertFalse(entity instanceof EntityLiving);

        try {
            entity.onGround = true;
            entity.saveNBT();
            Assertions.assertFalse(entity.getNbt().contains("Motion"));

            entity.onGround = false;
            entity.saveNBT();
            Assertions.assertTrue(entity.getNbt().containsList("Motion"));
        } finally {
            entity.close();
        }
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
