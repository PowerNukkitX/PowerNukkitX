package org.powernukkitx.block;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemShulkerBox;
import org.powernukkitx.tags.BlockTags;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockBrownShulkerBox extends BlockUndyedShulkerBox {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_SHULKER_BOX, Set.of(BlockTags.PNX_SHULKERBOX));

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownShulkerBox(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Item getShulkerBox() {
        return new ItemShulkerBox(12);
    }
}