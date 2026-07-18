package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedButton extends BlockWoodenButton {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_BUTTON, CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedButton(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Warped Button";
    }

    @Override
    public int getBurnChance() {
        return -1;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }
}