package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemJungleSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockJungleWallSign extends BlockWallSign {
    public static final BlockProperties $1 = new BlockProperties(JUNGLE_WALL_SIGN, FACING_DIRECTION);
    /**
     * @deprecated 
     */
    

    public BlockJungleWallSign() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockJungleWallSign(BlockState blockState) {
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
        return JUNGLE_WALL_SIGN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getStandingSignId() {
        return JUNGLE_STANDING_SIGN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Jungle Wall Sign";
    }

    @Override
    public Item toItem() {
        return new ItemJungleSign();
    }
}
