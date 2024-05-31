package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedWarpedStem extends BlockStemStripped {
    public static final BlockProperties $1 = new BlockProperties(STRIPPED_WARPED_STEM, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockStrippedWarpedStem() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockStrippedWarpedStem(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Stripped Warped Stem";
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