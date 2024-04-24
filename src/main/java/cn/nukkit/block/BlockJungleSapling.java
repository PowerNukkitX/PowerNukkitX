package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockJungleSapling extends BlockSapling {
     public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_SAPLING, CommonBlockProperties.AGE_BIT);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockJungleSapling() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockJungleSapling(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getWoodType() {
        return WoodType.JUNGLE;
    }
}