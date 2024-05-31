package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMobSpawner extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(MOB_SPAWNER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMobSpawner() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMobSpawner(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Monster Spawner";
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
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 25;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
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
    
    public int getWaterloggingLevel() {
        return 1;
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
