package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author Nukkit Project Team
 */
public class BlockBookshelf extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(BOOKSHELF);
    /**
     * @deprecated 
     */
    

    public BlockBookshelf() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBookshelf(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Bookshelf";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1.5D;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 7.5D;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 30;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(ItemID.BOOK, 0, 3)
        };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }
}
