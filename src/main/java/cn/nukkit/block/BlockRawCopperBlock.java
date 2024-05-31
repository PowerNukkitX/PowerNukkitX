package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRawCopperBlock extends BlockRaw {
    public static final BlockProperties $1 = new BlockProperties(RAW_COPPER_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockRawCopperBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockRawCopperBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Block of Raw Copper";
    }
}