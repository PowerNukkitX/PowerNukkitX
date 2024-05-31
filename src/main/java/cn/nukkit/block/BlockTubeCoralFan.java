package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTubeCoralFan extends BlockCoralFan {
    public static final BlockProperties $1 = new BlockProperties(TUBE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockTubeCoralFan() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockTubeCoralFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Tube Coral Fan";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadTubeCoralFan();
    }
}