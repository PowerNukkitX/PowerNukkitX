package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooDoubleSlab extends BlockDoubleSlabBase {
    public BlockBambooDoubleSlab() {}

    public int getId() {
        return BAMBOO_DOUBLE_SLAB;
    }

    @PowerNukkitOnly
    @Override
    public String getSlabName() {
        return "Bamboo";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getSingleSlabId() {
        return BAMBOO_SLAB;
    }
}
