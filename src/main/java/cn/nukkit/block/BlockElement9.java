package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement9 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_9");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement9() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement9(BlockState blockstate) {
        super(blockstate);
    }
}