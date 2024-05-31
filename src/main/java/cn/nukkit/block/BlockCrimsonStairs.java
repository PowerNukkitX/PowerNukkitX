package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonStairs extends BlockStairsWood {
    public static final BlockProperties $1 = new BlockProperties(CRIMSON_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonStairs() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCrimsonStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Crimson Wood Stairs";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 0;
    }
}