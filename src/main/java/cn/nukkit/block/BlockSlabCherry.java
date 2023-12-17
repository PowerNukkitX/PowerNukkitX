package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;


public class BlockSlabCherry extends BlockSlab {


    public BlockSlabCherry() {
        this(0);
    }


    public BlockSlabCherry(int meta) {
        super(meta, DOUBLE_CHERRY_SLAB);
    }

    @Override
    public int getId() {
        return CHERRY_SLAB;
    }


    @Override
    public String getSlabName() {
        return "Cherry";
    }


    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId();
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
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}