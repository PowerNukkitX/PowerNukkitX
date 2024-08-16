package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockFireCoralWallFan extends BlockCoralWallFan {
     public static final BlockProperties PROPERTIES = new BlockProperties(FIRE_CORAL_WALL_FAN, CommonBlockProperties.CORAL_DIRECTION);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockFireCoralWallFan() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockFireCoralWallFan(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public String getName() {
        return "Fire Coral";
    }

    @Override
    public Block getDeadCoralFan() {
        return new BlockDeadFireCoralWallFan();
    }
}