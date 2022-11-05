package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

@Since("1.6.0.0-PNX")
@PowerNukkitOnly
public class BlockPowderSnow extends BlockTransparentMeta {
    @Override
    public String getName() {
        return "Powder Snow";
    }

    @Override
    public int getId() {
        return BlockID.POWDER_SNOW;
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 0.1;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SNOW_BLOCK_COLOR;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity.getFreezingTicks() < 140)
            entity.addFreezingTicks(1);
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
}
