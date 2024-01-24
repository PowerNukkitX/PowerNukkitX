package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockChemistryTable extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHEMISTRY_TABLE, CommonBlockProperties.CHEMISTRY_TABLE_TYPE, CommonBlockProperties.DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChemistryTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChemistryTable(BlockState blockstate) {
        super(blockstate);
    }
}