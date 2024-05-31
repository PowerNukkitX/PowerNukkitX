package cn.nukkit.block;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;


public class BlockMossCarpet extends BlockCarpet {

    public static final BlockProperties $1 = new BlockProperties(MOSS_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMossCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMossCarpet(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Moss Carpet";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.1;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{toItem()};
    }
}
