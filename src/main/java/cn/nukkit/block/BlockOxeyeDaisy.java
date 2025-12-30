package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOxeyeDaisy extends BlockFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(OXEYE_DAISY);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxeyeDaisy() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockOxeyeDaisy(BlockState blockstate) {
        super(blockstate);
    }
}