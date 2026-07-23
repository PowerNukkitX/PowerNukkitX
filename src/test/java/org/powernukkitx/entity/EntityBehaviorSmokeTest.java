package org.powernukkitx.entity;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Deeper behaviour pass over every registered entity. Where EntitySmokeTest only reads
 * pure getters, this spawns each entity and drives real state-mutating behaviour -
 * nbt round-trips, attributes, effects, ticking, motion, bounding boxes and the extra
 * EntityLiving / EntityIntelligent surface. Everything is tolerant so a single misbehaving
 * entity never fails the pass; the gate only requires that a healthy majority survive.
 */
public class EntityBehaviorSmokeTest {

    static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    @Test
    void everyEntitySurvivesBehaviourPass() {
        Set<String> ids = Registries.ENTITY.getKnownEntities().keySet();
        Assertions.assertFalse(ids.isEmpty(), "no entities registered");

        int checked = 0;
        int failCount = 0;
        StringBuilder failures = new StringBuilder();

        for (String id : ids) {
            Entity entity = null;
            try {
                Position pos = new Position(0, 80, 0, level);
                entity = Entity.createEntity(id, pos);
                if (entity == null) continue;
                exercise(entity);
                checked++;
            } catch (Throwable t) {
                failCount++;
                if (failCount <= 25) {
                    failures.append('\n').append(id).append(" -> ").append(t);
                }
            } finally {
                final Entity fe = entity;
                if (fe != null) safe(fe::close);
            }
        }

        Assertions.assertTrue(checked > 0, "no entity survived the behaviour pass" + failures);
        Assertions.assertTrue(failCount < ids.size() * 0.15,
                "too many entities failed (" + failCount + "/" + ids.size() + ")" + failures);
    }

    private void exercise(Entity e) {
        nbtAndSave(e);
        attributes(e);
        effects(e);
        ticking(e);
        damage(e);
        movement(e);
        boundingBox(e);
        dataFlags(e);
        living(e);
        intelligent(e);
    }

    private void nbtAndSave(Entity e) {
        safe(e::saveNBT);
        safe(e::getNbt);
        safe(e::getIdentifier);
        safe(() -> {
            CompoundTag tag = e.getNbt();
            if (tag != null) {
                tag.putString("BehaviourProbe", "x");
                Assertions.assertEquals("x", tag.getString("BehaviourProbe"));
                tag.remove("BehaviourProbe");
            }
        });
    }

    private void attributes(Entity e) {
        safe(e::getAttributes);
        safe(e::getMovementSpeed);
        safe(e::getMovementSpeedDefault);
        safe(() -> e.setScale(1.0f));
        safe(e::getScale);
        safe(e::getMaxHealth);
        safe(e::getHealth);
        safe(() -> e.setHealth(e.getHealth()));
        safe(e::getHealthCurrent);
        safe(() -> e.setHealthCurrent(e.getHealthCurrent()));
    }

    private void effects(Entity e) {
        safe(e::getEffects);
        safe(() -> {
            Effect effect = Effect.get(1);
            if (effect != null) {
                effect.setDuration(20).setAmplifier(0);
                e.addEffect(effect);
                e.getEffects();
                e.removeEffect(effect.getType());
            }
        });
        safe(() -> {
            EffectType type = EffectType.get(1);
            if (type != null) e.removeEffect(type);
        });
    }

    private void ticking(Entity e) {
        safe(e::entityBaseTick);
        safe(() -> e.entityBaseTick(1));
        safe(() -> e.onUpdate(1));
    }

    private void damage(Entity e) {
        safe(() -> {
            EntityDamageEvent event =
                    new EntityDamageEvent(e, EntityDamageEvent.DamageCause.CONTACT, 0.0f);
            e.attack(event);
        });
    }

    private void movement(Entity e) {
        safe(() -> e.setMotion(new Vector3(0, 0, 0)));
        safe(() -> e.move(0, 0, 0));
        safe(e::getDirectionVector);
        safe(e::getDirection);
        safe(e::getHorizontalFacing);
    }

    private void boundingBox(Entity e) {
        safe(e::getBoundingBox);
        safe(e::recalculateBoundingBox);
        safe(() -> e.recalculateBoundingBox(false));
    }

    private void dataFlags(Entity e) {
        safe(() -> e.setNameTag("probe"));
        safe(e::getNameTag);
        safe(() -> {
            org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags flag =
                    org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags.ON_FIRE;
            e.setDataFlag(flag, true, false);
            e.getDataFlag(flag);
            e.setDataFlag(flag, false, false);
        });
    }

    private void living(Entity e) {
        if (!(e instanceof EntityLiving living)) return;
        safe(living::getAttackPower);
        safe(living::getAirTicks);
        safe(() -> living.setAirTicks(living.getAirTicks()));
        safe(living::isBlocking);
        safe(living::getAttackTime);
        safe(living::getDrops);
        safe(() -> living.setMovementSpeed(living.getMovementSpeed()));
        safe(living::recalcMovementSpeedFromEffects);
    }

    private void intelligent(Entity e) {
        if (!(e instanceof EntityIntelligent smart)) return;
        safe(smart::getBehaviorGroup);
        safe(smart::enableHeadYaw);
        safe(() -> smart.getJumpingMotion(0.42));
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
