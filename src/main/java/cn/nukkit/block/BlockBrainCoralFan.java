package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrainCoralFan extends BlockCoralFan {
    public static final BlockProperties $1 = new BlockProperties(BRAIN_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBrainCoralFan() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBrainCoralFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Brain Coral Fan";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadBrainCoralFan();
    }
}