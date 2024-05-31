package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement52 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_52");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement52() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement52(BlockState blockstate) {
        super(blockstate);
    }
}