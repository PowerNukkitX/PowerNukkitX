package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockAllium extends BlockFlower {
    public static final BlockProperties $1 = new BlockProperties(ALLIUM);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockAllium() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockAllium(BlockState blockstate) {
        super(blockstate);
    }
}