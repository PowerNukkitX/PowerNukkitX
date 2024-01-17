package cn.nukkit.level;

import cn.nukkit.GameMockExtension;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.generator.Flat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;

@ExtendWith(GameMockExtension.class)
public class GeneratorTest {
    @Test
    void testCreate(LevelProvider levelProvider) {
        Flat flat = new Flat(new HashMap<>());
        IChunk chunk = levelProvider.getChunk(0, 0, true);
        flat.asyncGenerate(chunk);
    }
}
