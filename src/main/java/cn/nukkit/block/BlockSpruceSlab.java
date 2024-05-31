package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceSlab extends BlockWoodenSlab {
    public static final BlockProperties $1 = new BlockProperties(SPRUCE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSpruceSlab(BlockState blockstate) {
        super(blockstate, SPRUCE_DOUBLE_SLAB);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Spruce";
    }
}