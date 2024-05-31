package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement76 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_76");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement76() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement76(BlockState blockstate) {
        super(blockstate);
    }
}