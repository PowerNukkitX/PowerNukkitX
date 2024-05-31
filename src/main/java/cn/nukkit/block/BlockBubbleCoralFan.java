package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBubbleCoralFan extends BlockCoralFan {
    public static final BlockProperties $1 = new BlockProperties(BUBBLE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBubbleCoralFan() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBubbleCoralFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Bubble Coral Fan";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadBubbleCoralFan();
    }
}