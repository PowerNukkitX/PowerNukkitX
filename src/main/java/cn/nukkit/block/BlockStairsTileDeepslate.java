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
public class BlockStairsTileDeepslate extends BlockStairs {

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockStairsTileDeepslate() {
        this(0);
    }
    
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockStairsTileDeepslate(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return DEEPSLATE_TILE_STAIRS;
    }
    
    @Override
    public String getName() {
        return "Deepslate Tile Stairs";
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
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY;
    }
}
