package org.powernukkitx.level.generator.object.legacytree;

import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LegacyJungleTree extends LegacyTreeGenerator {
    @Override
    public WoodType getType() {
        return WoodType.JUNGLE;
    }

    @Override
    public void placeObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {
        this.treeHeight = random.nextInt(6) + 4;
        super.placeObject(level, x, y, z, random);
    }
}
