package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockTubeCoral extends BlockCoral {
    public static final BlockProperties $1 = new BlockProperties(TUBE_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockTubeCoral() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockTubeCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isDead() {
        return false;
    }

    @Override
    public Block getDeadCoral() {
        return new BlockDeadTubeCoral();
    }
}