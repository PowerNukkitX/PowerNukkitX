package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(LIGHT_BLUE_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlueConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlueConcrete(BlockState blockstate) {
        super(blockstate);
    }
}