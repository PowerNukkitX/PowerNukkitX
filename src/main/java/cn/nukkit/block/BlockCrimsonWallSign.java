package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCrimsonSign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockCrimsonWallSign extends BlockWallSign {
    public static final BlockProperties $1 = new BlockProperties(CRIMSON_WALL_SIGN, FACING_DIRECTION);
    /**
     * @deprecated 
     */
    

    public BlockCrimsonWallSign() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonWallSign(BlockState blockState) {
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
        return CRIMSON_WALL_SIGN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getStandingSignId() {
        return CRIMSON_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return new ItemCrimsonSign();
    }
}
