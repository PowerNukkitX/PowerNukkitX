package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BlockSculk extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(SCULK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSculk() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSculk(BlockState blockstate) {
        super(blockstate);
    }
    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Sculk";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double calculateBreakTime(@NotNull Item item, @Nullable Player player) {
        if (canHarvest(item)) {
            return super.calculateBreakTime(item, player);
        } else {
            return 1;
        }
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
