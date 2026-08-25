package jmh;

import org.powernukkitx.Player;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.BlockTurtleEgg;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.IMemoryStorage;
import org.powernukkitx.entity.ai.sensor.BlockSensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

/**
 * Measures the per-tick cost of the mob AI framework against the real fixture world:
 * the behavior scheduler, the sensors every mob runs, and the memory storage they all hit.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Threads(1)
@Fork(1)
public class MobAIBenchmark {

    private static final int PLAYER_COUNT = 8;
    private static final int NEARBY_MOB_COUNT = 24;

    private Level level;
    private EntityIntelligent zombie;
    private IBehaviorGroup group;
    private IMemoryStorage memory;

    private BlockSensor blockSensor;
    private NearestPlayerSensor playerSensor;
    private NearestTargetEntitySensor<Entity> targetSensor;

    @Setup
    public void setup() throws Exception {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        level.getChunk(0, 0, true);
        var stone = Registries.BLOCKSTATE.getAllState().stream()
                .filter(state -> state.getIdentifier().equals("minecraft:stone")).findFirst().orElseThrow();
        for (int x = -16; x <= 16; x++) {
            for (int z = -16; z <= 16; z++) {
                level.setBlock(new Vector3(x, 63, z), stone.toBlock());
            }
        }

        zombie = (EntityIntelligent) Entity.createEntity("minecraft:zombie", new Position(0.5, 64, 0.5, level));
        group = zombie.getBehaviorGroup();
        memory = zombie.getMemoryStorage();

        //the sensors a vanilla zombie carries, rebuilt here so each one can be measured on its own
        blockSensor = new BlockSensor(BlockTurtleEgg.class, CoreMemoryTypes.NEAREST_BLOCK, 11, 15, 10);
        playerSensor = new NearestPlayerSensor(40, 0, 0);
        targetSensor = new NearestTargetEntitySensor<>(0, 16, 20,
                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), target -> true);

        spawnCrowd();
    }

    //the sensors only get interesting once there is something to find, so fill the area
    @SuppressWarnings("unchecked")
    private void spawnCrowd() throws Exception {
        var players = (Map<Long, Player>) FieldUtils.readField(level, "players", true);
        for (int i = 0; i < PLAYER_COUNT; i++) {
            Player player = mock(Player.class);
            FieldUtils.writeField(player, "x", 4.0 + i, true);
            FieldUtils.writeField(player, "y", 64.0, true);
            FieldUtils.writeField(player, "z", 4.0 + i, true);
            players.put((long) i, player);
        }
        for (int i = 0; i < NEARBY_MOB_COUNT; i++) {
            Entity.createEntity("minecraft:pig", new Position(2.5 + (i % 8), 64, 2.5 + (i / 8.0), level));
        }
    }

    @Benchmark
    public void aiTick_wholePipeline() {
        group.collectSensorData(zombie);
        group.evaluateCoreBehaviors(zombie);
        group.evaluateBehaviors(zombie);
        group.tickRunningCoreBehaviors(zombie);
        group.tickRunningBehaviors(zombie);
        group.applyController(zombie);
    }

    @Benchmark
    public void scheduler_evaluateBehaviors() {
        group.evaluateBehaviors(zombie);
    }

    @Benchmark
    public void scheduler_tickRunningBehaviors() {
        group.tickRunningBehaviors(zombie);
    }

    @Benchmark
    public void sensor_block() {
        blockSensor.sense(zombie);
    }

    @Benchmark
    public void sensor_nearestPlayer() {
        playerSensor.sense(zombie);
    }

    @Benchmark
    public void sensor_nearestTargetEntity() {
        memory.clear(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET);
        targetSensor.sense(zombie);
    }

    @Benchmark
    public void route_searchFlatAStar(Blackhole hole) {
        var routeFinder = group.getRouteFinder();
        routeFinder.setStart(new Vector3(0.5, 64, 0.5));
        routeFinder.setTarget(new Vector3(12.5, 64, 12.5));
        hole.consume(routeFinder.search());
    }

    @Benchmark
    public void memory_get(Blackhole hole) {
        hole.consume(memory.get(CoreMemoryTypes.NEAREST_PLAYER));
    }

    @Benchmark
    public void memory_putGet(Blackhole hole) {
        memory.put(CoreMemoryTypes.LAST_ATTACK_TIME, 1);
        hole.consume(memory.get(CoreMemoryTypes.LAST_ATTACK_TIME));
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include(MobAIBenchmark.class.getSimpleName())
                .build()).run();
    }
}
