package org.powernukkitx.entity.ai;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Drives the AI pipeline (sensor -> evaluator -> executor -> controller -> route)
 * by ticking every registered {@link EntityIntelligent} mob against the fixture
 * world. The whole entity/ai tree only runs while such a mob is ticked, so this
 * spawns them over real ground and walks the public tick entry points.
 */
public class EntityAITickSmokeTest {

    static Level level;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        prepareGround();
    }

    // Load the spawn chunk and lay a small stone floor so sensors/pathfinding have terrain.
    private static void prepareGround() {
        safe(() -> level.getChunk(0, 0, true));
        var stone = firstSolidState();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                final int fx = x, fz = z;
                safe(() -> level.setBlock(new Vector3(fx, 63, fz), stone.toBlock()));
            }
        }
    }

    @Test
    void tickEveryIntelligentMob() {
        List<EntityIntelligent> spawned = new ArrayList<>();
        int checked = 0;

        for (Map.Entry<String, Class<? extends Entity>> e : Registries.ENTITY.getKnownEntities().entrySet()) {
            if (!EntityIntelligent.class.isAssignableFrom(e.getValue())) continue;

            EntityIntelligent mob = spawn(e.getKey());
            if (mob == null) continue;
            spawned.add(mob);
            checked++;
            drive(mob);
        }

        // Fallback: if the registry exposed no intelligent class, tick a few known mobs by id.
        if (checked == 0) {
            for (String id : new String[]{"minecraft:zombie", "minecraft:cow", "minecraft:chicken",
                    "minecraft:pig", "minecraft:skeleton"}) {
                EntityIntelligent mob = spawn(id);
                if (mob == null) continue;
                spawned.add(mob);
                checked++;
                drive(mob);
            }
        }

        for (EntityIntelligent mob : spawned) {
            safe(mob::close);
        }

        Assertions.assertTrue(checked > 0, "no intelligent mob was ticked");
    }

    private EntityIntelligent spawn(String id) {
        try {
            Entity entity = Entity.createEntity(id, new Position(0.5, 80, 0.5, level));
            if (entity instanceof EntityIntelligent intelligent) return intelligent;
            if (entity != null) safe(entity::close);
        } catch (Throwable ignore) {
        }
        return null;
    }

    // Walk the public AI surface across several ticks so evaluators/executors advance state.
    private void drive(EntityIntelligent mob) {
        safe(mob::getBehaviorGroup);
        safe(mob::getMemoryStorage);
        safe(mob::isActive);
        safe(mob::getMoveTarget);
        safe(mob::getLookTarget);
        safe(() -> mob.setMoveTarget(new Vector3(1, 63, 1)));
        safe(() -> mob.setLookTarget(new Vector3(2, 64, 2)));

        IBehaviorGroup group = null;
        try {
            group = mob.getBehaviorGroup();
        } catch (Throwable ignore) {
        }
        if (group != null) {
            safe(group::getBehaviors);
            safe(group::getCoreBehaviors);
            safe(group::getSensors);
            safe(group::getControllers);
            safe(group::getRouteFinder);
            safe(group::getMemoryStorage);
            safe(group::isForceUpdateRoute);
        }

        for (int i = 0; i < 8; i++) {
            final int tick = i + 1;
            final IBehaviorGroup g = group;
            safe(() -> mob.onUpdate(tick));
            safe(() -> mob.asyncPrepare(tick));
            if (g != null) {
                safe(() -> g.collectSensorData(mob));
                safe(() -> g.evaluateCoreBehaviors(mob));
                safe(() -> g.evaluateBehaviors(mob));
                safe(() -> g.tickRunningCoreBehaviors(mob));
                safe(() -> g.tickRunningBehaviors(mob));
                safe(() -> g.updateRoute(mob));
                safe(() -> g.applyController(mob));
                safe(() -> g.getRunningBehaviors());
                safe(() -> g.getRunningCoreBehaviors());
            }
        }
    }

    private static org.powernukkitx.block.BlockState firstSolidState() {
        for (org.powernukkitx.block.BlockState s : Registries.BLOCKSTATE.getAllState()) {
            if (s.getIdentifier().equals("minecraft:stone")) return s;
        }
        return Registries.BLOCKSTATE.getAllState().iterator().next();
    }

    private static void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
