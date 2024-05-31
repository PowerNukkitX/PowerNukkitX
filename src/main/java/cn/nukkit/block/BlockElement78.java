package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement78 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_78");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement78() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement78(BlockState blockstate) {
        super(blockstate);
    }
}