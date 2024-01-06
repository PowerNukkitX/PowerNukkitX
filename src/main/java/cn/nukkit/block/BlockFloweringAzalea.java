package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockFloweringAzalea extends BlockAzalea {

    public static final BlockProperties PROPERTIES = new BlockProperties(FLOWERING_AZALEA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFloweringAzalea() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFloweringAzalea(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "FloweringAzalea";
    }

}
