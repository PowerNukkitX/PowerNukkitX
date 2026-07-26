package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemJungleSign;
import org.jetbrains.annotations.NotNull;

public class BlockJungleStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getWallSignId() {
        return BlockJungleWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemJungleSign();
    }
}