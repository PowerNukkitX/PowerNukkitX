package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

public class BlockMushroomStem extends BlockMushroomBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(MUSHROOM_STEM, CommonBlockProperties.HUGE_MUSHROOM_BITS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMushroomStem() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMushroomStem(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
}