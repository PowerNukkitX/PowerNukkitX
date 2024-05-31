package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoweredComparator extends BlockRedstoneComparator {
    public static final BlockProperties $1 = new BlockProperties(POWERED_COMPARATOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OUTPUT_LIT_BIT, CommonBlockProperties.OUTPUT_SUBTRACT_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPoweredComparator() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPoweredComparator(BlockState blockstate) {
        super(blockstate);
        this.isPowered = true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Comparator Block Powered";
    }

    @Override
    public BlockRedstoneComparator getPowered() {
        return this;
    }
}