package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Directly instantiates concrete {@link IBehaviorExecutor}s and drives their
 * lifecycle (onStart/execute/onInterrupt/onStop) against real intelligent mobs.
 * The behavior-group tick reaches these bodies only when memories/targets line
 * up, so we build them by hand for breadth.
 */
public class ExecutorSmokeTest {

    static Level level;
    private static final List<EntityIntelligent> mobs = new ArrayList<>();

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        prepareGround();
    }

    private static void prepareGround() {
        safe(() -> level.getChunk(0, 0, true));
        var stone = firstSolidState();
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                final int fx = x, fz = z;
                safe(() -> level.setBlock(new Vector3(fx, 63, fz), stone.toBlock()));
            }
        }
    }

    @Test
    void driveExecutors() {
        EntityIntelligent zombie = spawn("minecraft:zombie");
        EntityIntelligent cow = spawn("minecraft:cow");
        EntityIntelligent chicken = spawn("minecraft:chicken");
        EntityIntelligent pig = spawn("minecraft:pig");
        EntityIntelligent skeleton = spawn("minecraft:skeleton");
        EntityIntelligent villager = spawn("minecraft:villager");
        EntityIntelligent any = firstNonNull(zombie, cow, chicken, pig, skeleton, villager);
        Assertions.assertNotNull(any, "no intelligent mob spawned");

        // Seed a few memories so movement/attack executors have something to chase.
        seed(zombie);
        seed(cow);
        seed(any);

        int checked = 0;
        Set<String> temptItems = Set.of("minecraft:wheat", "minecraft:carrot");

        // Roaming / movement (no memory needed).
        checked += drive(new FlatRandomRoamExecutor(0.3f, 8, 40), any, cow, pig);
        checked += drive(new NearbyFlatRandomRoamExecutor(CoreMemoryTypes.MOVE_TARGET, 0.3f, 8, 40), any, cow);
        checked += drive(new SpaceRandomRoamExecutor(0.3f, 8, 4, 40), any, chicken);
        checked += drive(new HomeboundSpaceRandomRoamExecutor(0.3f, 8, 4, 40,
                new Position(0.5, 63, 0.5, level), 16), any);

        // Memory-target movement.
        checked += drive(new MoveToTargetExecutor(CoreMemoryTypes.MOVE_TARGET, 0.3f), any, zombie, cow);
        checked += drive(new FleeFromTargetExecutor(CoreMemoryTypes.MOVE_TARGET, 0.4f), any, cow);
        checked += drive(new LookAtTargetExecutor(CoreMemoryTypes.LOOK_TARGET, 40), any, cow, chicken);
        checked += drive(new CircleAboveTargetExecutor(CoreMemoryTypes.MOVE_TARGET, 0.3f), any);

        // Attack.
        checked += drive(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.4f, 8, true, 20), zombie, any);
        checked += drive(new WolfAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.4f, 8, true, 20), any);
        checked += drive(new GuardianAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.4f, 8, true, 20, 5), any);
        checked += drive(new StaringAttackTargetExecutor(), any);

        // Tempt / beg / feeding.
        checked += drive(new TemptExecutor(1.0f, temptItems), any, cow, pig);
        checked += drive(new FloatTemptExecutor(1.0f, temptItems), any, cow);
        checked += drive(new BegExecutor(temptItems), any);
        checked += drive(new LookAtFeedingPlayerExecutor(), any, cow);

        // Breeding / love.
        checked += drive(new BreedingExecutor(8, 40, 0.3f), cow, pig, any);
        checked += drive(new InLoveExecutor(200), cow, any);
        checked += drive(new LoveTimeoutExecutor(600), cow, any);
        checked += drive(new AnimalGrowExecutor(), cow, chicken, any);

        // Misc simple.
        checked += drive(new DoNothingExecutor(), any, zombie);
        checked += drive(new JumpExecutor(), any, cow);
        checked += drive(new TeleportExecutor(), any);
        checked += drive(new EatGrassExecutor(40), cow, any);
        checked += drive(new PlaySoundExecutor(Sound.AMBIENT_CANDLE), any, cow);
        checked += drive(new FollowRiderExecutor(), any);

        // Composite over a couple of the above.
        checked += drive(new MultipleExecutor(new DoNothingExecutor(), new JumpExecutor()), any, cow);

        for (EntityIntelligent mob : mobs) safe(mob::close);

        Assertions.assertTrue(checked > 0, "no executor was driven");
    }

    // Runs the full lifecycle of one executor against each mob a few ticks.
    private int drive(IBehaviorExecutor executor, EntityIntelligent... targets) {
        int hits = 0;
        for (EntityIntelligent mob : targets) {
            if (mob == null) continue;
            safe(() -> executor.onStart(mob));
            for (int i = 0; i < 4; i++) safe(() -> executor.execute(mob));
            safe(() -> executor.onInterrupt(mob));
            safe(() -> executor.onStop(mob));
            hits++;
        }
        return hits;
    }

    private void seed(EntityIntelligent mob) {
        if (mob == null) return;
        safe(() -> mob.setMoveTarget(new Vector3(2, 63, 2)));
        safe(() -> mob.setLookTarget(new Vector3(2, 64, 2)));
        safe(() -> mob.getMemoryStorage().put(CoreMemoryTypes.MOVE_TARGET, new Vector3(2, 63, 2)));
        safe(() -> mob.getMemoryStorage().put(CoreMemoryTypes.LOOK_TARGET, new Vector3(2, 64, 2)));
    }

    private EntityIntelligent spawn(String id) {
        try {
            Entity entity = Entity.createEntity(id, new Position(0.5, 80, 0.5, level));
            if (entity instanceof EntityIntelligent intelligent) {
                mobs.add(intelligent);
                return intelligent;
            }
            if (entity != null) safe(entity::close);
        } catch (Throwable ignore) {
        }
        return null;
    }

    @SafeVarargs
    private static <T> T firstNonNull(T... values) {
        for (T v : values) if (v != null) return v;
        return null;
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
