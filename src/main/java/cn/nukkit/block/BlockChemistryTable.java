package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockChemistryTable extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:chemistry_table", CommonBlockProperties.CHEMISTRY_TABLE_TYPE, CommonBlockProperties.DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChemistryTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChemistryTable(BlockState blockstate) {
        super(blockstate);
    }
}