package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRawIronBlock extends Block {
    public static final BlockProperties $1 = new BlockProperties(RAW_IRON_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockRawIronBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockRawIronBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Block of Raw Iron";
    }
}