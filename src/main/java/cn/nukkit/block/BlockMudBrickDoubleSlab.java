package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMudBrickDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties $1 = new BlockProperties(MUD_BRICK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMudBrickDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMudBrickDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Double Mud Brick Slab";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSingleSlabId() {
        return MUD_BRICK_SLAB;
    }

}