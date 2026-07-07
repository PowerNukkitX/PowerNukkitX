package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenButton extends BlockButton {
    public static final BlockProperties PROPERTIES = new BlockProperties(WOODEN_BUTTON, CommonBlockProperties.BUTTON_PRESSED_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWoodenButton() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWoodenButton(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Oak Button";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}