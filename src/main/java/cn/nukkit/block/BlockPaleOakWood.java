package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockPaleOakWood extends BlockWood {
     public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_WOOD, CommonBlockProperties.PILLAR_AXIS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockPaleOakWood(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getWoodType() {
        return WoodType.PALE_OAK;
    }
}