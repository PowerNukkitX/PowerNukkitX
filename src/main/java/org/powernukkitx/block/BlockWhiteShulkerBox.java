package org.powernukkitx.block;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemShulkerBox;
import org.powernukkitx.tags.BlockTags;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockWhiteShulkerBox extends BlockUndyedShulkerBox {
    public static final BlockProperties PROPERTIES = new BlockProperties(WHITE_SHULKER_BOX, Set.of(BlockTags.PNX_SHULKERBOX));

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteShulkerBox(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Item getShulkerBox() {
        return new ItemShulkerBox(0);
    }
}