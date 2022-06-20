package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockMossCarpet extends BlockCarpet {
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final BlockProperties PROPERTIES = CommonBlockProperties.EMPTY_PROPERTIES;

    @Override
    public int getId() {
        return MOSS_CARPET;
    }

    @Override
    public double getResistance() {
        return 0.1;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }

    @Override
    public String getName() {
        return "Moss Carpet";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRASS_BLOCK_COLOR;
    }

    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
}
