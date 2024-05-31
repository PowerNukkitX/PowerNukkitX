package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement41 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_41");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement41() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement41(BlockState blockstate) {
        super(blockstate);
    }
}