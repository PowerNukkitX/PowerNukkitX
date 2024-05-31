package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockVerdantFroglight extends BlockFroglight {

    public static final BlockProperties $1 = new BlockProperties(VERDANT_FROGLIGHT,
            PILLAR_AXIS);
    /**
     * @deprecated 
     */
    

    public BlockVerdantFroglight() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockVerdantFroglight(BlockState blockState) {
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
        return "Verdant Froglight";
    }
}
