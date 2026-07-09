package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemPlayerHead;
import org.jetbrains.annotations.NotNull;

public class BlockPlayerHead extends BlockHead {

    public static final BlockProperties PROPERTIES = new BlockProperties(PLAYER_HEAD, CommonBlockProperties.FACING_DIRECTION);

    public BlockPlayerHead(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Player Head";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                this.toItem()
        };
    }

    @Override
    public Item toItem() {
        Item item = new ItemPlayerHead();
        item.setBlockUnsafe(this.getProperties().getDefaultState().toBlock());
        return item;
    }
}
