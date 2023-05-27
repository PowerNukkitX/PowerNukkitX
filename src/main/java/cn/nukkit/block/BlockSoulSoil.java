package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockSoulSoil extends BlockSolid {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockSoulSoil() {
        // Does nothing
    }

    @Override
    public int getId() {
        return SOUL_SOIL;
    }

    @Override
    public String getName() {
        return "Soul Soil";
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @PowerNukkitOnly
    @Override
    public boolean isSoulSpeedCompatible() {
        return true;
    }
}
