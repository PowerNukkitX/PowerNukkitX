package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockInfoUpdate extends Block {
    public static final BlockProperties $1 = new BlockProperties(INFO_UPDATE);
    /**
     * @deprecated 
     */
    

    public BlockInfoUpdate() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockInfoUpdate(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Info Update Block";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

}
