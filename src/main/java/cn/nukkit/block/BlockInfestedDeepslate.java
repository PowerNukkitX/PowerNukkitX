package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockInfestedDeepslate extends BlockSolid {

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockInfestedDeepslate() {
        // Does nothing
    }
    
    @Override
    public int getId() {
        return INFESTED_DEEPSLATE;
    }
    
    @Override
    public String getName() {
        return "Infested Deepslate";
    }
    
    @Override
    public double getHardness() {
        return 0;
    }
    
    @Override
    public double getResistance() {
        return 0.75;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
    
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY;
    }
}
