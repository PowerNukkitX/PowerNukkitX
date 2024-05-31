package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockFireCoralFan extends BlockCoralFan {
     public static final BlockProperties $1 = new BlockProperties(FIRE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockFireCoralFan() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockFireCoralFan(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Fire Coral Fan";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadFireCoralFan();
    }
}