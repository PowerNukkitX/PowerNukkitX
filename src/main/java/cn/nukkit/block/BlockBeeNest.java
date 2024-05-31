package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockBeeNest extends BlockBeehive {
    public static final BlockProperties $1 = new BlockProperties(BEE_NEST, CommonBlockProperties.DIRECTION, CommonBlockProperties.HONEY_LEVEL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBeeNest() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBeeNest(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Bee Nest";
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
        return 60;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 1.5;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

}
