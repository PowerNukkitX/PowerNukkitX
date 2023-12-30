package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

/**
 * @author Angelic47 (Nukkit Project)
 */

public class BlockFurnace extends BlockFurnaceBurning {

    public static final BlockProperties PROPERTIES = new BlockProperties(FURNACE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFurnace() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFurnace(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Furnace";
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
