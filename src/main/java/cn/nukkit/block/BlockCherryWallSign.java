package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCherrySign;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockCherryWallSign extends BlockWallSign {
    public static final BlockProperties $1 = new BlockProperties(CHERRY_WALL_SIGN, FACING_DIRECTION);
    /**
     * @deprecated 
     */
    

    public BlockCherryWallSign() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCherryWallSign(BlockState blockState) {
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
        return CHERRY_WALL_SIGN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getStandingSignId() {
        return CHERRY_STANDING_SIGN;
    }

    @Override
    public Item toItem() {
        return new ItemCherrySign();
    }
}
