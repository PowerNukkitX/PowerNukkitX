package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement18 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_18");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement18() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement18(BlockState blockstate) {
        super(blockstate);
    }
}