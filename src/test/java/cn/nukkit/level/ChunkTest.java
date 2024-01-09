package cn.nukkit.level;

import cn.nukkit.BlockRegistryExtension;
import cn.nukkit.LevelDBProviderExtension;
import cn.nukkit.block.BlockDiamondOre;
import cn.nukkit.block.BlockGoldOre;
import cn.nukkit.block.BlockOre;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.LevelDBProvider;
import cn.nukkit.registry.BlockRegistry;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@ExtendWith({BlockRegistryExtension.class, LevelDBProviderExtension.class})
public class ChunkTest {
    @Test
    void testSetBlockState(LevelDBProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(0, 0);
        chunk.setBlockState(0, 100, 0, BlockGoldOre.PROPERTIES.getDefaultState());
        Assertions.assertEquals(BlockGoldOre.PROPERTIES.getDefaultState(), chunk.getBlockState(0, 100, 0));
    }

    @Test
    void test_recalculateHeightMap(LevelDBProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(0, 0);
        chunk.recalculateHeightMap();
    }

    @Test
    void test_recalculateHeightMapColumn(LevelDBProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(0, 0);
        chunk.recalculateHeightMapColumn(0, 0);
    }

    @Test
    void test_populateSkyLight(LevelDBProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(0, 0);
        chunk.populateSkyLight();
    }

    @Test
    void testMutiThreadOperate(LevelDBProvider levelDBProvider) {
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
                throw new RuntimeException(e);
            }
        });
    }
}
