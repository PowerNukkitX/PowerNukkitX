package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(HARD_STAINED_GLASS, CommonBlockProperties.COLOR);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHardStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHardStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}