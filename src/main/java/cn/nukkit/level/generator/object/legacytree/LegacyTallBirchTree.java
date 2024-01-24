package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.RandomSource;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LegacyTallBirchTree extends LegacyBirchTree {

    @Override
    public void placeObject(BlockManager level, int x, int y, int z, RandomSource random) {
        this.treeHeight = random.nextInt(3) + 10;
        super.placeObject(level, x, y, z, random);
    }
}
