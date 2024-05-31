package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSpruceSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockSpruceWallSign extends BlockWallSign {
    public static final BlockProperties $1 = new BlockProperties(SPRUCE_WALL_SIGN, FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSpruceWallSign() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSpruceWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Spruce Wall Sign";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getWallSignId() {
        return SPRUCE_WALL_SIGN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getStandingSignId() {
        return SPRUCE_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return new ItemSpruceSign();
    }
}
