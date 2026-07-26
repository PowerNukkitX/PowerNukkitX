package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

public abstract class ObjectGenerator implements BlockID {
    public abstract boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position);
}
