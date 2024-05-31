package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHornCoralFan extends BlockCoralFan {
     public static final BlockProperties $1 = new BlockProperties(HORN_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockHornCoralFan() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockHornCoralFan(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Horn Coral Fan";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadHornCoralFan();
    }
}