    package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;


public class BlockMangroveLog extends BlockLog {
    public static final BlockProperties $1 = new BlockProperties(MANGROVE_LOG, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMangroveLog() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMangroveLog(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Mangrove Log";
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedMangroveLog.PROPERTIES.getDefaultState();
    }
}
