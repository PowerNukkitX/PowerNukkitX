package org.powernukkitx.level.generator.object.legacytree;

import org.powernukkitx.block.BlockOrangePoplarLeaves;
import org.powernukkitx.block.BlockRedPoplarLeaves;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockYellowPoplarLeaves;
import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Poplar trees keep the classic oak shape, only the foliage colour changes per tree.
 */
public class LegacyPoplarTree extends LegacyTreeGenerator {
    private BlockState leafState = BlockYellowPoplarLeaves.PROPERTIES.getDefaultState();

    @Override
    public WoodType getType() {
        return WoodType.POPLAR;
    }

    @Override
    public void placeObject(BlockManager level, int x, int y, int z, RandomSourceProvider random) {
        this.leafState = switch (random.nextInt(3)) {
            case 0 -> BlockRedPoplarLeaves.PROPERTIES.getDefaultState();
            case 1 -> BlockOrangePoplarLeaves.PROPERTIES.getDefaultState();
            default -> BlockYellowPoplarLeaves.PROPERTIES.getDefaultState();
        };
        this.treeHeight = random.nextInt(3) + 5;

        super.placeObject(level, x, y, z, random);
    }

    @Override
    protected BlockState getLeafBlockState() {
        return this.leafState;
    }
}
