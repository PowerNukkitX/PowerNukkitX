package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakSapling extends BlockSapling {
     public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_SAPLING, CommonBlockProperties.AGE_BIT);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockDarkOakSapling() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockDarkOakSapling(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getWoodType() {
        return WoodType.DARK_OAK;
    }
}