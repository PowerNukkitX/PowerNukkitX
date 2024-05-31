package cn.nukkit.block;

import cn.nukkit.block.property.enums.StoneType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockStone extends BlockSolid{
    public static final BlockProperties $1 = new BlockProperties(STONE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockStone() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockStone(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    public StoneType stoneType() {
        return StoneType.STONE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            return new Item[]{
                    StoneType.STONE.equals(stoneType())
                            ? Item.get(BlockID.COBBLESTONE)
                            : toItem()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }
}
