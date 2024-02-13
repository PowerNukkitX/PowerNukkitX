package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

public class BlockRedMushroomBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_MUSHROOM_BLOCK, CommonBlockProperties.HUGE_MUSHROOM_BITS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedMushroomBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedMushroomBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
        return 0.2;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (new NukkitRandom().nextInt(1, 20) == 1) {
            return new Item[]{
                    Item.get(RED_MUSHROOM_BLOCK)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}