package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 */
public class BlockPiston extends BlockPistonBase {
    public static final BlockProperties $1 = new BlockProperties(PISTON, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPiston() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPiston(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Piston";
    }

    @Override
    public Block createHead(BlockFace blockFace) {
        return new BlockPistonArmCollision().setPropertyValue(CommonBlockProperties.FACING_DIRECTION,blockFace.getIndex());
    }
}
