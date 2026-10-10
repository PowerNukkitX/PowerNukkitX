package org.powernukkitx;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.config.category.LevelSettings;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.GameLoop;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Who ticks a spawned player, on which thread and how often, driven through the real
 * {@code Server#checkTickUpdates} and the real fixture {@link Level}.
 */
@ExtendWith(GameMockExtension.class)
class ServerPlayerTickTest {
    private static final long RUNTIME_ID = 0x7E57_0001L;
    private static final int THROTTLED_TICK_RATE = 5;
    private static final int LEVEL_THREAD_TICKS = 50;

    private record Call(Thread thread, int tick) {
    }

    private final List<Call> calls = new CopyOnWriteArrayList<>();
    private final List<Thread> networkChecks = new CopyOnWriteArrayList<>();

    private Server server;
    private Level level;
    private LevelSettings levelSettings;
    private Player player;

    private Object savedPlayers;
    private Object savedLevelArray;
    private Object savedLevelThreadMode;
    private boolean savedAlwaysTickPlayers;

    @BeforeEach
    void setUp(Server server, Level level) {
        this.server = server;
        this.level = level;
        this.levelSettings = server.getSettings().levelSettings();

        this.savedPlayers = readField("players");
        this.savedLevelArray = readField("levelArray");
        this.savedLevelThreadMode = readField("levelThreadMode");
        this.savedAlwaysTickPlayers = this.levelSettings.alwaysTickPlayers();

        this.player = mock(Player.class);
        this.player.spawned = true;
        doReturn(level).when(this.player).getLevel();
        doReturn(RUNTIME_ID).when(this.player).runtimeId();
        doAnswer(invocation -> {
            this.calls.add(new Call(Thread.currentThread(), invocation.getArgument(0)));
            return true;
        }).when(this.player).onUpdate(anyInt());
        doAnswer(invocation -> {
            this.networkChecks.add(Thread.currentThread());
            return null;
        }).when(this.player).checkNetwork();
        level.addEntity(this.player);
        level.updateEntities.put(RUNTIME_ID, this.player);

        Map<InetSocketAddress, Player> players = new ConcurrentHashMap<>();
        players.put(new InetSocketAddress("127.0.0.1", 19132), this.player);
        TestUtils.setField(Server.class, server, "players", players);
        TestUtils.setField(Server.class, server, "levelArray", new Level[]{level});
    }

    @AfterEach
    void tearDown() {
        this.level.removeEntity(this.player);
        this.level.setTickRate(1);
        this.level.tickRateCounter = 0;
        this.level.getBaseTickGameLoop().setRunning(false);
        this.level.tickRateTime = 0;
        this.level.tickRateTimeNanos = 0;
        doReturn(0L).when(this.server).getNanosPerTick();
        this.levelSettings.alwaysTickPlayers(this.savedAlwaysTickPlayers);
        doReturn(false).when(this.server).isLevelThreadMode();
        TestUtils.setField(Server.class, this.server, "players", this.savedPlayers);
        TestUtils.setField(Server.class, this.server, "levelArray", this.savedLevelArray);
        TestUtils.setField(Server.class, this.server, "levelThreadMode", this.savedLevelThreadMode);
    }

    @Test
    void sharedModeTicksPlayersOfThrottledLevelOncePerServerTick() {
        useSharedMode(false);
        throttleLevel();

        // tickRateCounter 5 -> 4..1 are skipped, the fifth server tick runs Level#doTick
        int firstTick = 10_000;
        for (int tick = firstTick; tick < firstTick + THROTTLED_TICK_RATE; tick++) {
            checkTickUpdates(tick);
        }

        assertEquals(List.of(firstTick, firstTick + 1, firstTick + 2, firstTick + 3, firstTick + 4),
            this.calls.stream().map(Call::tick).toList());
        assertTrue(this.calls.stream().allMatch(call -> call.thread() == Thread.currentThread()));
        assertEquals(THROTTLED_TICK_RATE, this.networkChecks.size());
    }

    @Test
    void sharedModeAlwaysTickPlayersDoesNotDoubleTickOnSkippedLevelTick() {
        useSharedMode(true);
        throttleLevel();

        checkTickUpdates(20_000);

        assertEquals(List.of(20_000), this.calls.stream().map(Call::tick).toList());
    }

    @Test
    void sharedModeOnlyChecksNetworkOfUnspawnedPlayersOfThrottledLevel() {
        useSharedMode(false);
        throttleLevel();
        this.player.spawned = false;

        checkTickUpdates(30_000);

        assertTrue(this.calls.isEmpty());
        assertEquals(1, this.networkChecks.size());
    }

    @Test
    void sharedModeReleasesTickCacheOfThrottledLevel() {
        useSharedMode(false);
        throttleLevel();
        Vector3 pos = new Vector3(3, 90, 3);
        this.level.setBlock(pos, Block.get(BlockID.AIR));
        assertEquals(BlockID.AIR, this.level.getTickCachedBlock(pos).getId());

        // A player action between level ticks changes the world
        this.level.setBlock(pos, Block.get(BlockID.STONE));
        checkTickUpdates(40_000);

        assertEquals(BlockID.STONE, this.level.getTickCachedBlock(pos).getId());
        this.level.setBlock(pos, Block.get(BlockID.AIR));
    }

    @Test
    void levelThreadModeOnlyTheLevelThreadTicksPlayers() throws InterruptedException {
        this.levelSettings.alwaysTickPlayers(true);
        // tickRate only scales the daylight cycle in this mode, the level loop never skips
        throttleLevel();

        Thread levelThread = runLevelThreadAlongsideServerLoop(LEVEL_THREAD_TICKS);

        assertEquals(LEVEL_THREAD_TICKS, this.calls.size());
        assertTrue(this.calls.stream().allMatch(call -> call.thread() == levelThread));
    }

    @Test
    void levelThreadModeOnlyTheServerLoopChecksNetwork() throws InterruptedException {
        runLevelThreadAlongsideServerLoop(LEVEL_THREAD_TICKS);

        assertTrue(!this.networkChecks.isEmpty());
        assertTrue(this.networkChecks.stream().allMatch(thread -> thread == Thread.currentThread()));
    }

    @Test
    void levelThreadModeMeasuresTicksAgainstServerBudget() {
        TestUtils.setField(Server.class, this.server, "levelThreadMode", true);
        doReturn(true).when(this.server).isLevelThreadMode();
        doReturn(1L).when(this.server).getNanosPerTick();
        GameLoop loop = this.level.getBaseTickGameLoop();
        loop.setRunning(true);

        loop.tick();

        assertTrue(this.level.tickRateTimeNanos > 0);
        assertTrue(this.level.getTickRate() > 1, "a tick over budget must throttle the level");
    }

    /**
     * Drives the real level loop on its own thread, as level thread mode does, while this thread keeps
     * running the server loop until the level thread is done.
     */
    private Thread runLevelThreadAlongsideServerLoop(int levelTicks) throws InterruptedException {
        TestUtils.setField(Server.class, this.server, "levelThreadMode", true);
        doReturn(true).when(this.server).isLevelThreadMode();
        GameLoop loop = this.level.getBaseTickGameLoop();
        loop.setRunning(true);
        CountDownLatch serverLoopRunning = new CountDownLatch(1);
        AtomicBoolean levelThreadDone = new AtomicBoolean();
        Thread levelThread = new Thread(() -> {
            try {
                serverLoopRunning.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            for (int i = 0; i < levelTicks; i++) {
                loop.tick();
            }
            levelThreadDone.set(true);
        }, "Level Thread - test");
        levelThread.start();

        // The level thread only starts ticking once the server loop has, so the two always overlap
        int serverTick = 50_000;
        do {
            checkTickUpdates(serverTick++);
            serverLoopRunning.countDown();
        } while (!levelThreadDone.get() && levelThread.isAlive());
        levelThread.join();
        return levelThread;
    }

    private void useSharedMode(boolean alwaysTickPlayers) {
        TestUtils.setField(Server.class, this.server, "levelThreadMode", false);
        this.levelSettings.alwaysTickPlayers(alwaysTickPlayers);
    }

    private void throttleLevel() {
        this.level.setTickRate(THROTTLED_TICK_RATE);
        this.level.tickRateCounter = THROTTLED_TICK_RATE;
    }

    private Object readField(String name) {
        return TestUtils.getField(Server.class, this.server, name);
    }

    private void checkTickUpdates(int currentTick) {
        TestUtils.checkTickUpdates(this.server, currentTick);
    }
}
