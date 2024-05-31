package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaSapling extends BlockSapling {
     public static final BlockProperties $1 = new BlockProperties(ACACIA_SAPLING, CommonBlockProperties.AGE_BIT);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

    public BlockAcaciaSapling() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

     public BlockAcaciaSapling(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getWoodType() {
        return WoodType.ACACIA;
    }
}