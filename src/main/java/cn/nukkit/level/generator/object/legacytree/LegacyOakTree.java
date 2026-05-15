package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.ObjectFancyOakTree;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LegacyOakTree extends LegacyTreeGenerator {

    @Override
    public WoodType getType() {
        return WoodType.OAK;
    }

    @Override
    public void placeObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {
        if (this.hasFancyBrace(level, x, y, z)) {
            new ObjectFancyOakTree().generate(level, random, new Vector3(x, y, z));
            return;
        }

        this.treeHeight = random.nextInt(3) + 4;
        super.placeObject(level, x, y, z, random);
    }

    @Override
    public boolean canPlaceObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {
        return this.hasFancyBrace(level, x, y, z) || super.canPlaceObject(level, x, y, z, random);
    }

    private boolean hasFancyBrace(BlockManager level, int x, int y, int z) {
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                if (xx == 0 && zz == 0) {
                    continue;
                }

                if (!this.overridable(level.getBlockIfCachedOrLoaded(x + xx, y + 1, z + zz))) {
                    return true;
                }
            }
        }

        return false;
    }
}
