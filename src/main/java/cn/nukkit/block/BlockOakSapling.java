package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockOakSapling extends BlockSapling {
     public static final BlockProperties PROPERTIES = new BlockProperties(OAK_SAPLING, CommonBlockProperties.AGE_BIT);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockOakSapling() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockOakSapling(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getWoodType() {
        return WoodType.OAK;
    }
}