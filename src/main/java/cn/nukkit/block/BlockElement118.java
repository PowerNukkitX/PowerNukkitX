package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement118 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_118");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement118() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement118(BlockState blockstate) {
        super(blockstate);
    }
}