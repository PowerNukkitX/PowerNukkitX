package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadHornCoral extends BlockHornCoral {
    public static final BlockProperties $1 = new BlockProperties(DEAD_HORN_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeadHornCoral() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeadHornCoral(BlockState blockstate) {
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