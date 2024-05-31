package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement95 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_95");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement95() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement95(BlockState blockstate) {
        super(blockstate);
    }
}