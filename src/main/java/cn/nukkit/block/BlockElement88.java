package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement88 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_88");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement88() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement88(BlockState blockstate) {
        super(blockstate);
    }
}