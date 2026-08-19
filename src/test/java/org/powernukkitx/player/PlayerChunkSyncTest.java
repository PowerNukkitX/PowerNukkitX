package org.powernukkitx.player;

import org.powernukkitx.PlayerFixture;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.event.player.PlayerTeleportEvent;
import org.powernukkitx.level.ChunkLoader;
import org.powernukkitx.level.DimensionEnum;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelConfig;
import org.powernukkitx.level.format.leveldb.LevelDBProvider;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.GameLoop;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.apache.commons.io.FileUtils;
import org.cloudburstmc.protocol.bedrock.packet.LevelChunkPacket;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.HashMap;

class PlayerChunkSyncTest {

    private static TestPlayer player;
    private static Level homeLevel;
    private static Level otherLevel;
    private static File otherLevelDir;

    @BeforeAll
    static void boot() throws Exception {

        player = PlayerFixture.newPlayer();
        homeLevel = ServerMockFixture.level;

        Mockito.doReturn(true).when(player.getSession()).isConnected();
        player.loggedIn = true;
        player.spawned = true;

        player.temporalVector = new Vector3();

        otherLevel = openSecondLevel();
    }

    @AfterAll
    static void tearDown() {
        if (player != null) {
            player.unloadAllUsedChunk();
            Mockito.doReturn(false).when(player.getSession()).isConnected();
        }
        if (otherLevel != null) {
            try {
                otherLevel.close();
            } catch (Throwable ignore) {
            }
            otherLevel = null;
        }
        if (otherLevelDir != null) {
            FileUtils.deleteQuietly(otherLevelDir);
            otherLevelDir = null;
        }
    }

    @Test
    void aChunkTheManagerStoppedTrackingIsNotRecordedAsReceived() {
        player.setLevel(homeLevel);
        player.setPosition(new Vector3(0.5, 80, 0.5));

        int chunkX = player.getChunkX() + 64;
        int chunkZ = player.getChunkZ() + 64;
        long hash = Level.chunkHash(chunkX, chunkZ);
        Assertions.assertFalse(player.getPlayerChunkManager().isSentChunk(hash));

        player.sendChunk(chunkX, chunkZ, new LevelChunkPacket());

        Assertions.assertFalse(player.getUsedChunks().contains(hash),
                "a chunk the player is not registered as a loader for must not be marked as received");
    }

    @Test
    void aCrossLevelTeleportLeavesEveryReceivedChunkRegistered() throws InterruptedException {

        generateAround(otherLevel, player.getViewDistance() + 1);

        player.setLevel(homeLevel);
        player.setPosition(new Vector3(0.5, 80, 0.5));

        Assertions.assertTrue(player.teleport(
                new Location(0.5, 80, 0.5, otherLevel),
                PlayerTeleportEvent.TeleportCause.PLUGIN));
        Assertions.assertSame(otherLevel, player.getLevel());
        Assertions.assertFalse(player.getUsedChunks().isEmpty(),
                "the teleport should have sent the player chunks of the destination");
        assertEveryReceivedChunkIsRegistered();

        final GameLoop loop = GameLoop.builder().build();
        for (int i = 0; i < 20; i++) {
            otherLevel.subTick(loop);
            assertEveryReceivedChunkIsRegistered();
            player.getPlayerChunkManager().tick();
            assertEveryReceivedChunkIsRegistered();
        }
    }

    private static void assertEveryReceivedChunkIsRegistered() {
        final Level level = player.getLevel();
        final LongOpenHashSet received;
        synchronized (player.getPlayerChunkManager()) {
            received = new LongOpenHashSet(player.getPlayerChunkManager().getUsedChunks());
        }
        for (long hash : received) {
            int chunkX = Level.getHashX(hash);
            int chunkZ = Level.getHashZ(hash);
            boolean registered = false;
            for (ChunkLoader loader : level.getChunkLoaders(chunkX, chunkZ)) {
                if (loader == player) {
                    registered = true;
                    break;
                }
            }
            Assertions.assertTrue(registered, "chunk " + chunkX + ", " + chunkZ
                    + " is marked as received but the player is not registered as a loader for it");
        }
    }

    private static void generateAround(Level level, int radius) throws InterruptedException {
        final long deadline = System.currentTimeMillis() + 60_000;
        for (int chunkX = -radius; chunkX <= radius; chunkX++) {
            for (int chunkZ = -radius; chunkZ <= radius; chunkZ++) {
                IChunk chunk = level.getChunk(chunkX, chunkZ, true);
                while (!chunk.getChunkState().canSend() || !chunk.isInitiated()) {
                    Assertions.assertTrue(System.currentTimeMillis() < deadline,
                            "timed out generating chunk " + chunkX + ", " + chunkZ);
                    level.generateChunk(chunkX, chunkZ, true);
                    Thread.sleep(5);
                    chunk = level.getChunk(chunkX, chunkZ, true);
                }
            }
        }
    }

    private static Level openSecondLevel() throws Exception {

        String name = "chunk_sync_" + ProcessHandle.current().pid() + "_" + System.nanoTime();
        otherLevelDir = new File("src/test/resources/" + name);
        FileUtils.copyDirectory(new File("src/test/resources/level"), otherLevelDir);

        Level level = new Level(ServerMockFixture.server, name, otherLevelDir.getPath(), 1,
                LevelDBProvider.class,
                new LevelConfig.GeneratorConfig("flat", 114514L, false, LevelConfig.AntiXrayMode.LOW,
                        true, DimensionEnum.OVERWORLD.getDimensionData(), new HashMap<>()));
        level.initLevel();
        return level;
    }
}
