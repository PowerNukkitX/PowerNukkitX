package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadTubeCoral extends BlockTubeCoral {
    public static final BlockProperties $1 = new BlockProperties(DEAD_TUBE_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeadTubeCoral() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeadTubeCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isDead() {
        return true;
    }
}