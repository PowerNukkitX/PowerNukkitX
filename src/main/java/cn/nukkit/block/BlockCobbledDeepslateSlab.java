package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCobbledDeepslateSlab extends BlockSlab {
    public static final BlockProperties $1 = new BlockProperties(COBBLED_DEEPSLATE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCobbledDeepslateSlab() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCobbledDeepslateSlab(BlockState blockstate) {
        super(blockstate, COBBLED_DEEPSLATE_DOUBLE_SLAB);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Cobbled Deepslate";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }
}