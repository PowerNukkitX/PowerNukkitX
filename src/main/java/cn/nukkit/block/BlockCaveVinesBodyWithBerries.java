package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.GROWING_PLANT_AGE;


public class BlockCaveVinesBodyWithBerries extends BlockCaveVines {
    public static final BlockProperties PROPERTIES = new BlockProperties(CAVE_VINES_BODY_WITH_BERRIES, GROWING_PLANT_AGE);

    public BlockCaveVinesBodyWithBerries() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCaveVinesBodyWithBerries(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cave Vines Body With Berries";
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
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
