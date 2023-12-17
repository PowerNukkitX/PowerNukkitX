package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;


public class BlockShroomlight extends BlockTransparent {


    public BlockShroomlight() {
        // Does nothing
    }

    @Override
    public int getId() {
        return SHROOMLIGHT;
    }

    @Override
    public String getName() {
        return "Shroomlight";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HANDS_ONLY; //TODO Should be hoe, fix at https://github.com/PowerNukkit/PowerNukkit/pull/367
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

}
