package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockFrostedIce extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:frosted_ice", CommonBlockProperties.AGE_4);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFrostedIce() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFrostedIce(BlockState blockstate) {
        super(blockstate);
    }
}