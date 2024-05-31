package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCamera extends Block {
    public static final BlockProperties $1 = new BlockProperties(CAMERA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCamera() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCamera(BlockState blockstate) {
        super(blockstate);
    }
}