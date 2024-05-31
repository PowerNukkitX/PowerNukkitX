package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockGlowingobsidian extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(GLOWINGOBSIDIAN);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGlowingobsidian() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGlowingobsidian(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Glowing Obsidian";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 50;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 6000;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 12;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.OBSIDIAN));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() > ItemTool.TIER_DIAMOND) {
            return new Item[] { toItem() };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePushed() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public  boolean canBePulled() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }
}