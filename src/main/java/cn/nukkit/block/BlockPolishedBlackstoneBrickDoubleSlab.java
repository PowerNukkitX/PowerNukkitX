package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneBrickDoubleSlab extends BlockPolishedBlackstoneDoubleSlab {
    public static final BlockProperties $1 = new BlockProperties(POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedBlackstoneBrickDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedBlackstoneBrickDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Polished Blackstone Brick";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSingleSlabId() {
        return POLISHED_BLACKSTONE_BRICK_SLAB;
    }
}