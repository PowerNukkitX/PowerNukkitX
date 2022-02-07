package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockWallDeepslateCobbled extends BlockWallBase {

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockWallDeepslateCobbled() {
        this(0);
    }
    
    @PowerNukkitOnly
    @Since("FUTURE")
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
    
    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY;
    }
}
