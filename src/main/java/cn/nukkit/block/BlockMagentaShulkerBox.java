package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemShulkerBox;
import cn.nukkit.tags.BlockTags;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockMagentaShulkerBox extends BlockUndyedShulkerBox {
    public static final BlockProperties PROPERTIES = new BlockProperties(MAGENTA_SHULKER_BOX, Set.of(BlockTags.PNX_SHULKERBOX));

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaShulkerBox(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Item getShulkerBox() {
        return new ItemShulkerBox(2);
    }
}