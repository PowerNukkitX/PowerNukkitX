package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockPaleOakSapling extends BlockSapling {
     public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_SAPLING, CommonBlockProperties.AGE_BIT);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

    public BlockPaleOakSapling() {
        super(PROPERTIES.getDefaultState());
    }

     public BlockPaleOakSapling(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getWoodType() {
        return WoodType.PALE_OAK;
    }
}