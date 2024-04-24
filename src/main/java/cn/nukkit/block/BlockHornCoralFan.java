package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHornCoralFan extends BlockCoralFan {
     public static final BlockProperties PROPERTIES = new BlockProperties(HORN_CORAL_FAN, CommonBlockProperties.CORAL_FAN_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockHornCoralFan() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockHornCoralFan(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public String getName() {
        return "Horn Coral Fan";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadHornCoralFan();
    }
}