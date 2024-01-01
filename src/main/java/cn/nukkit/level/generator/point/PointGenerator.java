package cn.nukkit.level.generator.point;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSource;

public abstract class PointGenerator {
    public abstract boolean generate(BlockManager level, RandomSource rand, Vector3 pos);
}
