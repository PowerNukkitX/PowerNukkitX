package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBirchSign;
import org.jetbrains.annotations.NotNull;

public class BlockBirchStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(BIRCH_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected String getStandingSignId() {
        return PROPERTIES.getIdentifier();
    }

    @Override
    public String getWallSignId() {
        return BlockBirchWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemBirchSign();
    }
}