package org.powernukkitx.block;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBambooSign;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.FACING_DIRECTION;


public class BlockBambooWallSign extends BlockWallSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_WALL_SIGN, FACING_DIRECTION);

    public BlockBambooWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public String getName() {
        return "Bamboo Wall Sign";
    }

    @Override
    public String getStandingSignId() {
        return BAMBOO_STANDING_SIGN;
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