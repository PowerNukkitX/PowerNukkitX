package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement31 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_31");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement31() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement31(BlockState blockstate) {
        super(blockstate);
    }
}