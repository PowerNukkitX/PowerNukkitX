package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaSapling extends BlockSapling {
     public static final BlockProperties PROPERTIES = new BlockProperties(ACACIA_SAPLING, CommonBlockProperties.AGE_BIT);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockAcaciaSapling() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockAcaciaSapling(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getWoodType() {
        return WoodType.ACACIA;
    }
}