package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;

/**
 * @author GoodLucky777
 */


public class BlockWallDeepslateCobbled extends BlockWallBase {


    public BlockWallDeepslateCobbled() {
        this(0);
    }


    public BlockWallDeepslateCobbled(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return COBBLED_DEEPSLATE_WALL;
    }
    
    @Override
    public String getName() {
        return "Cobbled Deepslate Wall";
    }
    
    @Override
    public double getHardness() {
        return 3.5;
    }
    
    @Override
    public double getResistance() {
        return 6;
    }
    
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

}
