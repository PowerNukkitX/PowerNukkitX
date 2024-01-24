package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.GROWING_PLANT_AGE;


public class BlockCaveVinesHeadWithBerries extends BlockCaveVines {
    public static final BlockProperties PROPERTIES = new BlockProperties(CAVE_VINES_HEAD_WITH_BERRIES, GROWING_PLANT_AGE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCaveVinesHeadWithBerries() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCaveVinesHeadWithBerries(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cave Vines Head With Berries";
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public int getLightLevel() {
        return 14;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(ItemID.GLOW_BERRIES)};
    }
}
