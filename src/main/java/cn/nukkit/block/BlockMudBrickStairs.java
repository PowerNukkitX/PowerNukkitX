package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMudBrickStairs extends BlockStairs {
    public static final BlockProperties $1 = new BlockProperties(MUD_BRICK_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);
    /**
     * @deprecated 
     */
    

    public BlockMudBrickStairs() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMudBrickStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Mud Bricks Stair";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
}
