package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement64 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_64");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement64() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement64(BlockState blockstate) {
        super(blockstate);
    }
}