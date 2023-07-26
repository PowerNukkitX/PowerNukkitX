package cn.nukkit.level.particle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import org.junit.jupiter.api.Test;

class DustParticleTest {
    @Test
    void constructor() {
        DustParticle dustParticle = new DustParticle(new Vector3(0, 1, 0), BlockColor.SAND_BLOCK_COLOR);
        assertEquals(BlockColor.SAND_BLOCK_COLOR.getARGB(), dustParticle.data);
    }
}
