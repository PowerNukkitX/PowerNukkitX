package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockStickyPiston extends BlockPistonBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(STICKY_PISTON, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStickyPiston() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStickyPiston(BlockState blockstate) {
        super(blockstate);
        sticky = true;
    }

    @Override
    public Block createHead(BlockFace blockFace) {
        return new BlockStickyPistonArmCollision().setPropertyValue(CommonBlockProperties.FACING_DIRECTION, blockFace.getIndex());
    }

    @Override
    public String getName() {
        return "Sticky Piston";
    }
}