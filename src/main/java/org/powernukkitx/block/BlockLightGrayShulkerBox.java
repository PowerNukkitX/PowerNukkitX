package org.powernukkitx.block;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemShulkerBox;
import org.powernukkitx.tags.BlockTags;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockLightGrayShulkerBox extends BlockUndyedShulkerBox {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_GRAY_SHULKER_BOX, Set.of(BlockTags.PNX_SHULKERBOX));

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayShulkerBox(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Item getShulkerBox() {
        return new ItemShulkerBox(8);
    }
}