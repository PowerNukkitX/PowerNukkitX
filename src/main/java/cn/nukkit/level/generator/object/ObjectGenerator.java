package cn.nukkit.level.generator.object;

import cn.nukkit.block.BlockID;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public abstract class ObjectGenerator implements BlockID {
    public abstract boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position);
}
