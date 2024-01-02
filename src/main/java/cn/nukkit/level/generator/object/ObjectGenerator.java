package cn.nukkit.level.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSource;

public abstract class ObjectGenerator implements BlockID {
    public abstract boolean generate(BlockManager level, RandomSource rand, Vector3 position);
}
