package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(MAGENTA_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMagentaConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMagentaConcrete(BlockState blockstate) {
        super(blockstate);
    }
}