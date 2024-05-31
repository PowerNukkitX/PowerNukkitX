package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement110 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_110");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement110() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement110(BlockState blockstate) {
        super(blockstate);
    }
}