package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOakSapling extends Block {
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
}