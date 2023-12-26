package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockChemicalHeat extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:chemical_heat");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChemicalHeat() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChemicalHeat(BlockState blockstate) {
        super(blockstate);
    }
}