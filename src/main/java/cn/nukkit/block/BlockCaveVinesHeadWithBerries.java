package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

import javax.annotation.Nonnull;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class BlockCaveVinesHeadWithBerries extends BlockCaveVines {
    public static final IntBlockProperty AGE_PROPERTY = new IntBlockProperty("growing_plant_age", false, 25, 0);
    public static final BlockProperties PROPERTIES = new BlockProperties(AGE_PROPERTY);

    @Override
    public String getName() {
        return "Cave Vines Head With Berries";
    }

    @Override
    public int getId() {
        return CAVE_VINES_HEAD_WITH_BERRIES;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
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
    public boolean onActivate(@Nonnull Item item) {
        final Block tmp = new BlockCaveVinesBodyWithBerries();
        tmp.setPropertyValue(BlockCaveVinesBodyWithBerries.AGE_PROPERTY, this.getPropertyValue(AGE_PROPERTY));
        getLevel().setBlock(this, tmp, true, true);
        getLevel().dropItem(this, Item.get(ItemID.GLOW_BERRIES));
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(ItemID.GLOW_BERRIES)};
    }
}
