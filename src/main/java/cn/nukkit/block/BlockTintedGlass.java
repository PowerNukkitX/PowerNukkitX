package cn.nukkit.block;

import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockTintedGlass extends BlockGlass {
    public static final BlockProperties $1 = new BlockProperties(TINTED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockTintedGlass() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockTintedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Tinted Glass";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[] { toItem() };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return false;
    }
}