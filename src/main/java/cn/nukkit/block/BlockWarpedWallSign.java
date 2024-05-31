package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemWarpedSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockWarpedWallSign extends BlockWallSign {
    public static final BlockProperties $1 = new BlockProperties(WARPED_WALL_SIGN, FACING_DIRECTION);
    /**
     * @deprecated 
     */
    

    public BlockWarpedWallSign() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWarpedWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getWallSignId() {
        return WARPED_WALL_SIGN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getStandingSignId() {
        return WARPED_STANDING_SIGN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Warped Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemWarpedSign();
    }
}
