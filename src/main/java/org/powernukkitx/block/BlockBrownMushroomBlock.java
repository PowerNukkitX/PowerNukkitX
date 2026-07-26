package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockBrownMushroomBlock extends BlockMushroomBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_MUSHROOM_BLOCK, CommonBlockProperties.HUGE_MUSHROOM_BITS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownMushroomBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownMushroomBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Item toItem() {
        final Item item = super.toItem();
        item.setBlockUnsafe(item.getBlock().setPropertyValue(CommonBlockProperties.HUGE_MUSHROOM_BITS, 14));
        return item;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (ThreadLocalRandom.current().nextInt(1, 20) == 1) {
            return new Item[]{
                    Item.get(BROWN_MUSHROOM)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}