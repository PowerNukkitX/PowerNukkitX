package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockStairs;
import cn.nukkit.item.ItemTool;

@PowerNukkitOnly
public class BlockStairsPrismarine extends BlockStairs {
    @PowerNukkitOnly
    public BlockStairsPrismarine() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockStairsPrismarine(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PRISMARINE_STAIRS;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    @PowerNukkitOnly
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public String getName() {
        return "Prismarine Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
