package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement79 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_79");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement79() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement79(BlockState blockstate) {
        super(blockstate);
    }
}