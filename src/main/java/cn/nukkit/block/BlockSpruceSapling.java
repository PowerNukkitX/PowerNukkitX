package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceSapling extends BlockSapling {
     public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_SAPLING, CommonBlockProperties.AGE_BIT);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }


    public BlockSpruceSapling() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockSpruceSapling(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getWoodType() {
        return WoodType.SPRUCE;
    }
}