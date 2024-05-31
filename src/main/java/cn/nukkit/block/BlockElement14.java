package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement14 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_14");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement14() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement14(BlockState blockstate) {
        super(blockstate);
    }
}