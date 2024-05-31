package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement87 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_87");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement87() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement87(BlockState blockstate) {
        super(blockstate);
    }
}