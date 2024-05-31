package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement25 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_25");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement25() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement25(BlockState blockstate) {
        super(blockstate);
    }
}