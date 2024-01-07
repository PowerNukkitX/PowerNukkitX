package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemShulkerBox;
import cn.nukkit.tags.BlockTags;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockCyanShulkerBox extends BlockUndyedShulkerBox {
    public static final BlockProperties PROPERTIES = new BlockProperties(CYAN_SHULKER_BOX, Set.of(BlockTags.PNX_SHULKERBOX));

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanShulkerBox(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Item getShulkerBox() {
        return new ItemShulkerBox(9);
    }
}