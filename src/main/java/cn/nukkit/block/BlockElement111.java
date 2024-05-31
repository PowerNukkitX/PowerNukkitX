package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement111 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_111");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement111() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement111(BlockState blockstate) {
        super(blockstate);
    }
}