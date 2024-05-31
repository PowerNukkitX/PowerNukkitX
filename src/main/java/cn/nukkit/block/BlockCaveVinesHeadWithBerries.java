package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.GROWING_PLANT_AGE;


public class BlockCaveVinesHeadWithBerries extends BlockCaveVines {
    public static final BlockProperties $1 = new BlockProperties(CAVE_VINES_HEAD_WITH_BERRIES, GROWING_PLANT_AGE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCaveVinesHeadWithBerries() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCaveVinesHeadWithBerries(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Cave Vines Head With Berries";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isTransparent() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 14;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(ItemID.GLOW_BERRIES)};
    }
}
