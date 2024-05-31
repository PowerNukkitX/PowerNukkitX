package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement38 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_38");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement38() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement38(BlockState blockstate) {
        super(blockstate);
    }
}