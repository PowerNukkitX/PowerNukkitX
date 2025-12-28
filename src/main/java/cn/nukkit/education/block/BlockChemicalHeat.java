package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockChemicalHeat extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHEMICAL_HEAT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChemicalHeat() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChemicalHeat(BlockState blockstate) {
        super(blockstate);
    }
}