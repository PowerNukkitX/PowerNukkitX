package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMudBrickSlab extends BlockSlab{

    public static final BlockProperties $1 = new BlockProperties(MUD_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMudBrickSlab() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMudBrickSlab(BlockState blockState) {
        super(blockState, MUD_BRICK_DOUBLE_SLAB);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Mud Brick Slab";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }
}
