package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement20 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_20");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement20() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement20(BlockState blockstate) {
        super(blockstate);
    }
}