package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

public class BlockDriedKelpBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(DRIED_KELP_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDriedKelpBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDriedKelpBlock(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Dried Kelp Block";
    }

    @Override
    public double getHardness() {
        return 0.5F;
    }
    
    @Override
    public double getResistance() {
        return 2.5;
    }

}
