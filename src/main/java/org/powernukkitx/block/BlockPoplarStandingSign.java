package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemPoplarSign;
import org.jetbrains.annotations.NotNull;

public class BlockPoplarStandingSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_STANDING_SIGN, CommonBlockProperties.GROUND_SIGN_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoplarStandingSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoplarStandingSign(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected String getStandingSignId() {
        return PROPERTIES.getIdentifier();
    }

    @Override
    public String getWallSignId() {
        return BlockPoplarWallSign.PROPERTIES.getIdentifier();
    }

    @Override
    public Item toItem() {
        return new ItemPoplarSign();
    }
}
