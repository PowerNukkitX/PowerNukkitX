package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.item.ItemTool;

import static cn.nukkit.block.BlockID.BAMBOO_BLOCK;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooBlock extends BlockSolid {
    public BlockBambooBlock() {
    }

    public int getId() {
        return BAMBOO_BLOCK;
    }

    public String getName() {
        return "Bamboo Block";
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
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}