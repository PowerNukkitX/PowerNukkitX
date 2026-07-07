package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemHead;
import org.powernukkitx.item.ItemPiglinHead;
import org.jetbrains.annotations.NotNull;

public class BlockPiglinHead extends BlockHead implements ItemHead {

    public static final BlockProperties PROPERTIES = new BlockProperties(PIGLIN_HEAD, CommonBlockProperties.FACING_DIRECTION);

    public BlockPiglinHead(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Piglin Head";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem()
        };
    }

    @Override
    public Item toItem() {
        Item item = new ItemPiglinHead();
        item.setBlockUnsafe(this.getProperties().getDefaultState().toBlock());
        return item;
    }
}
