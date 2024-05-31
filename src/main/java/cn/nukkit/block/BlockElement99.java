package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement99 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_99");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement99() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement99(BlockState blockstate) {
        super(blockstate);
    }
}