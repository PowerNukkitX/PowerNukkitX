package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockBrownMushroomBlock extends BlockMushroomBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_MUSHROOM_BLOCK, CommonBlockProperties.HUGE_MUSHROOM_BITS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownMushroomBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownMushroomBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (ThreadLocalRandom.current().nextInt(1, 20) == 1) {
            return new Item[]{
                    Item.get(BROWN_MUSHROOM_BLOCK)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}