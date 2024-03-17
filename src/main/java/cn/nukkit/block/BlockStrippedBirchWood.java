package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedBirchWood extends BlockWoodStripped {
     public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_BIRCH_WOOD, CommonBlockProperties.PILLAR_AXIS);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockStrippedBirchWood(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getWoodType() {
        return WoodType.BIRCH;
    }
}