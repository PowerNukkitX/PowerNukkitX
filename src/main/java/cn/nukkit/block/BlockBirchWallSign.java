package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBirchSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockBirchWallSign extends BlockWallSign {
    public static final BlockProperties $1 = new BlockProperties(BIRCH_WALL_SIGN, FACING_DIRECTION);
    /**
     * @deprecated 
     */
    

    public BlockBirchWallSign() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBirchWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Birch Wall Sign";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getWallSignId() {
        return BIRCH_WALL_SIGN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getStandingSignId() {
        return BIRCH_WALL_SIGN;
    }

    @Override
    public Item toItem() {
        return new ItemBirchSign();
    }
}
