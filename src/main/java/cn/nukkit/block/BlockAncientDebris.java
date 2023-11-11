package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockAncientDebris extends BlockSolid {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockAncientDebris() {
        // Does nothing
    }

    @Override
    public int getId() {
        return ANCIENT_DEBRIS;
    }

    @Override
    public String getName() {
        return "Ancient Derbris";
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }
    
    @Override
    public double getResistance() {
        return 1200;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 30;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isLavaResistant() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
