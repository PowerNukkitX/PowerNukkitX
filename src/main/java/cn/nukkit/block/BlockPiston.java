package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 */
public class BlockPiston extends BlockPistonBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(PISTON, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPiston() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPiston(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Piston";
    }

    @Override
    public Block createHead(BlockFace blockFace) {
        return new BlockPistonArmCollision().setPropertyValue(CommonBlockProperties.FACING_DIRECTION,blockFace.getIndex());
    }
}
