package cn.nukkit.level;

import cn.nukkit.GameMockExtension;
import cn.nukkit.block.BlockDiamondOre;
import cn.nukkit.block.BlockGoldOre;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.LevelDBProvider;
import cn.nukkit.level.format.UnsafeChunk;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@ExtendWith({GameMockExtension.class})
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
    @SneakyThrows
    void test_getOrCreateSection(LevelDBProvider levelDBProvider) {
        IChunk chunk = levelDBProvider.getChunk(0, 0);
        Method getOrCreateSection = Chunk.class.getDeclaredMethod("getOrCreateSection", int.class);
        getOrCreateSection.setAccessible(true);
        ChunkSection s1 = (ChunkSection) getOrCreateSection.invoke(chunk, -4);
        Assertions.assertEquals(-4, s1.y());
        ChunkSection s2 = (ChunkSection) getOrCreateSection.invoke(chunk, 19);
        Assertions.assertEquals(19, s2.y());
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
