package org.powernukkitx.level;

import org.powernukkitx.GameMockExtension;
import org.powernukkitx.block.BlockDiamondOre;
import org.powernukkitx.block.BlockGoldOre;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.level.format.UnsafeChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.ItemHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({GameMockExtension.class})
public class ChunkTest {
    @Test
    void testSetBlockState(LevelProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(0, 0);
        chunk.setBlockState(0, 100, 0, BlockGoldOre.PROPERTIES.getDefaultState());
        Assertions.assertEquals(BlockGoldOre.PROPERTIES.getDefaultState(), chunk.getBlockState(0, 100, 0));
    }

    @Test
    void testSetBlockSkyLight(LevelProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(0, 0);
        chunk.setBlockSkyLight(0, 100, 0, 10);
        Assertions.assertEquals(10, chunk.getBlockSkyLight(0, 100, 0));
    }

    @Test
    void testSetBlockLight(LevelProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(0, 0);
        chunk.setBlockLight(0, 100, 0, 10);
        Assertions.assertEquals(10, chunk.getBlockLight(0, 100, 0));
    }

    @Test
    void testSetBiome(LevelProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(0, 0);
        chunk.setBiomeId(0, 100, 0, 10);
        Assertions.assertEquals(10, chunk.getBiomeId(0, 100, 0));
    }

    @Test
    void test_recalculateHeightMap(LevelProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(1000, 1000, true);
        levelDBProvider.getLevel().syncGenerateChunk(1000, 1000);
        chunk.recalculateHeightMap();
        Assertions.assertEquals(4, chunk.getHeightMap(0, 0));
    }

    @Test
    void test_recalculateHeightMapColumn(LevelProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(1000, 1000, true);
        levelDBProvider.getLevel().syncGenerateChunk(1000, 1000);
        chunk.recalculateHeightMapColumn(0, 0);
        Assertions.assertEquals(4, chunk.getHeightMap(0, 0));
    }

    @Test
    void test_populateSkyLight(LevelProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(1000, 1000, true);
        levelDBProvider.getLevel().syncGenerateChunk(1000, 1000);
        chunk.populateSkyLight();
        Assertions.assertEquals(15, chunk.getBlockSkyLight(0, 5, 0));
        Assertions.assertEquals(15, chunk.getBlockSkyLight(0, 6, 0));
    }

    @Test
    void testSaveAndReadChunkEntity(LevelProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(0, 0, true);
        Item item = Item.get(ItemID.GOLD_INGOT);
        EntityItem itemEntity = (EntityItem) Entity.createEntity(Entity.ITEM,
                chunk,
                Entity.getDefaultNBT(new Vector3(0, 64, 0),
                                new Vector3(0, 0, 0),
                                new Random().nextFloat() * 360, 0)
                        .putShort("Health", (short) 5)
                        .putCompound("Item", ItemHelper.write(item))
                        .putShort("PickupDelay", (short) 10)
        );
        chunk.addEntity(itemEntity);

        List<Entity> list = chunk.getEntities().values().stream().filter(e -> e.getIdentifier().equals(EntityID.ITEM)).toList();
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(EntityID.ITEM, list.get(0).getIdentifier());

        levelDBProvider.saveChunk(0, 0, chunk);
        IChunk newChunk = levelDBProvider.getChunk(0, 0);
        Assertions.assertNotNull(newChunk);
        List<Entity> list2 = chunk.getEntities().values().stream().filter(e -> e.getIdentifier().equals(EntityID.ITEM)).toList();
        Assertions.assertFalse(list2.isEmpty());
        Assertions.assertEquals(EntityID.ITEM, list2.get(0).getIdentifier());
    }

    @Test
    void test_getOrCreateSection(LevelProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(0, 0);
        ChunkSection[] sections = new ChunkSection[2];
        chunk.batchProcess(unsafeChunk -> {
            sections[0] = unsafeChunk.getOrCreateSection(-4);
            sections[1] = unsafeChunk.getOrCreateSection(19);
        });
        Assertions.assertNotNull(sections[0]);
        Assertions.assertNotNull(sections[1]);
        Assertions.assertEquals(-4, sections[0].y());
        Assertions.assertEquals(19, sections[1].y());
    }

    @Test
    void testMultiThreadOperate(LevelProvider levelDBProvider) {
        final IChunk chunk = levelDBProvider.getChunk(0, 0);
        Set<Thread> threadSet = new HashSet<>();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            if (i % 2 == 0) {
                Thread thread = new Thread(() -> {
                    chunk.batchProcess(unsafeChunk -> {
                        for (int k = 0; k < 16; k++) {
                            for (int l = 0; l < 16; l++) {
                                for (int j = unsafeChunk.getDimensionData().getMinHeight(); j < unsafeChunk.getDimensionData().getMaxHeight(); j++) {
                                    unsafeChunk.setBlockState(k, j, l, BlockGoldOre.PROPERTIES.getDefaultState(), 0);
                                    unsafeChunk.setBiomeId(k, j, l, BiomeID.BIRCH_FOREST_MUTATED);
                                }
                                unsafeChunk.setHeightMap(k, l, 4);
                            }
                        }
                    });
                });
                threadSet.add(thread);
                thread.start();
            } else {
                Thread thread = new Thread(() -> {
                    chunk.batchProcess(unsafeChunk -> {
                        for (int k = 0; k < 16; k++) {
                            for (int l = 0; l < 16; l++) {
                                for (int j = unsafeChunk.getDimensionData().getMinHeight(); j < unsafeChunk.getDimensionData().getMaxHeight(); j++) {
                                    unsafeChunk.setBlockState(k, j, l, BlockDiamondOre.PROPERTIES.getDefaultState(), 0);
                                    unsafeChunk.setBiomeId(k, j, l, BiomeID.DEEP_COLD_OCEAN);
                                    unsafeChunk.setBlockSkyLight(k, j, l, 4);
                                }
                            }
                        }
                    });
                });
                threadSet.add(thread);
                thread.start();
            }
        }
        threadSet.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        });
    }

    @Test
    void test_batchProcess_and_recalculateHeightMapColumn_no_deadlock(LevelProvider levelDBProvider) throws InterruptedException {
        final IChunk chunk = levelDBProvider.getChunk(0, 0, true);
        final Duration timeout = Duration.ofSeconds(5);

        final CountDownLatch bothReady = new CountDownLatch(2);
        final CountDownLatch release = new CountDownLatch(1);
        final AtomicBoolean batchProcessCompleted = new AtomicBoolean(false);
        final AtomicBoolean heightMapColumnCompleted = new AtomicBoolean(false);

        final ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.submit(() -> {
            awaitRelease(bothReady, release);
            chunk.batchProcess(UnsafeChunk::recalculateHeightMap);
            batchProcessCompleted.set(true);
        });

        pool.submit(() -> {
            awaitRelease(bothReady, release);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunk.recalculateHeightMapColumn(x, z);
                }
            }
            heightMapColumnCompleted.set(true);
        });

        bothReady.await(timeout.toSeconds(), TimeUnit.SECONDS);
        release.countDown();

        pool.shutdown();
        final boolean finishedInTime = pool.awaitTermination(timeout.toSeconds(), TimeUnit.SECONDS);
        if (!finishedInTime) {
            pool.shutdownNow();
        }

        assertTrue(finishedInTime, "Deadlock detected : batchProcessCompleted=" + batchProcessCompleted.get()
            + ", heightMapColumnCompleted=" + heightMapColumnCompleted.get());
    }

    private void awaitRelease(CountDownLatch bothReady, CountDownLatch release) {
        bothReady.countDown();
        try {
            release.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @SneakyThrows
    void test_SectionIsEmpty() {
        ChunkSection chunkSection = new ChunkSection((byte) 0);
        assertTrue(chunkSection.isEmpty());
    }
}
