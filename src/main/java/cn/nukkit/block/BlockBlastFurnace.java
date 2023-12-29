package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlastFurnace extends BlockBlastFurnaceBurning {

    public static final BlockProperties PROPERTIES = new BlockProperties(BLAST_FURNACE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlastFurnace() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlastFurnace(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Blast Furnace";
    }

    @Override
    public int getLightLevel() {
        return 0;
    }
}
