package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author Pub4Game
 * @since 21.02.2016
 */
public class BlockSlime extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(SLIME);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSlime() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSlime(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Slime Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightFilter() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSticksBlock() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return true;
    }
}
