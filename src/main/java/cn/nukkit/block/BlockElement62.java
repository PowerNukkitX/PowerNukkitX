package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement62 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_62");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement62() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement62(BlockState blockstate) {
        super(blockstate);
    }
}