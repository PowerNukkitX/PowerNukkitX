package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockFireCoralFan extends BlockCoralFan {
     public static final BlockProperties PROPERTIES = new BlockProperties(FIRE_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockFireCoralFan() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockFireCoralFan(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public String getName() {
        return "Fire Coral Fan";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadFireCoralFan();
    }
}