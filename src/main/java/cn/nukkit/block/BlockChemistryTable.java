package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockChemistryTable extends Block {
    public static final BlockProperties $1 = new BlockProperties(CHEMISTRY_TABLE, CommonBlockProperties.CHEMISTRY_TABLE_TYPE, CommonBlockProperties.DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockChemistryTable() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockChemistryTable(BlockState blockstate) {
        super(blockstate);
    }
}