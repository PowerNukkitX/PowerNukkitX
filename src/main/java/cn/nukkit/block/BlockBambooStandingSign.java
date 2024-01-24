package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBambooSign;
import org.jetbrains.annotations.NotNull;


public class BlockBambooStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getWallSignId() {
        return BlockBambooWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemBambooSign();
    }
}