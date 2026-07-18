package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemCherrySign;
import org.jetbrains.annotations.NotNull;

public class BlockCherryStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getWallSignId() {
        return BlockCherryWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemCherrySign();
    }
}