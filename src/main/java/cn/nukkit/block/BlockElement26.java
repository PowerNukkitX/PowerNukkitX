package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement26 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_26");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement26() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement26(BlockState blockstate) {
        super(blockstate);
    }
}