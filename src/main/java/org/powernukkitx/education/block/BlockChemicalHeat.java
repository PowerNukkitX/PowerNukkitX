package org.powernukkitx.education.block;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
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