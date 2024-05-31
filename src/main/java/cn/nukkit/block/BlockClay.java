package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemClayBall;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author Nukkit Project Team
 */
public class BlockClay extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(CLAY);
    /**
     * @deprecated 
     */
    

    public BlockClay() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockClay(BlockState blockState) {
        super(blockState);
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
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Clay Block";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public Item[] getDrops(Item item) {
        Item $2 = new ItemClayBall();
        clayBall.setCount(4);
        return new Item[]{
                clayBall
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
