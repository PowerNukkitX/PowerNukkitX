package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChemicalHeat extends Block {
    public static final BlockProperties $1 = new BlockProperties(CHEMICAL_HEAT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockChemicalHeat() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockChemicalHeat(BlockState blockstate) {
        super(blockstate);
    }
}