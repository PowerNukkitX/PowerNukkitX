package cn.nukkit.level;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.LevelDBProvider;
import cn.nukkit.registry.Registries;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class LevelDBProviderTest {
    static Level level = Mockito.mock(Level.class);
    static LevelDBProvider levelDBProvider;

    @BeforeAll
    @SneakyThrows
    static void create() {
        Registries.BLOCK.init();
        levelDBProvider = new LevelDBProvider(level, "src/test/resources/Bedrock level");
        levelDBProvider.initDimensionData(DimensionEnum.OVERWORLD.getDimensionData());
    }

    @Test
    void testLevelDatLoad() {
        Assertions.assertNotNull(levelDBProvider.getLevelData());
        Assertions.assertEquals(4, levelDBProvider.getLevelData().getServerChunkTickRange());
    }

    @Test
    void testLoadChunk() {
        IChunk chunk = levelDBProvider.getChunk(0, 0);
        Assertions.assertNotNull(chunk);
        Assertions.assertEquals("minecraft:cauldron", chunk.getBlockState(0, 284, 0).getIdentifier());
    }
}
