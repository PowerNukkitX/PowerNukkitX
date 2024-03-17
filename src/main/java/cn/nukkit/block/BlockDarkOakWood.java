package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakWood extends BlockWood {
     public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_WOOD, CommonBlockProperties.PILLAR_AXIS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockDarkOakWood(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getWoodType() {
        return WoodType.DARK_OAK;
    }
}