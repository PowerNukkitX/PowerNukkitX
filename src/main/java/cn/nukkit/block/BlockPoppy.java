package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPoppy extends BlockFlower {
    public static final BlockProperties $1 = new BlockProperties(POPPY);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPoppy() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPoppy(BlockState blockstate) {
        super(blockstate);
    }

}