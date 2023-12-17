package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;


public class BlockPlanksWarped extends BlockSolid {


    public BlockPlanksWarped() {
        // Does nothing
    }

    @Override
    public int getId() {
        return WARPED_PLANKS;
    }
    
    @Override
    public String getName() {
        return "Warped Planks";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }
}
