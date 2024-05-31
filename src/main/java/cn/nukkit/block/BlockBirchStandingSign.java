package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBirchSign;
import org.jetbrains.annotations.NotNull;

public class BlockBirchStandingSign extends BlockStandingSign {
    public static final BlockProperties $1 = new BlockProperties(BIRCH_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBirchStandingSign() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBirchStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected String getStandingSignId() {
        return PROPERTIES.getIdentifier();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getWallSignId() {
        return BlockBirchWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemBirchSign();
    }
}