package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockReeds extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:reeds", CommonBlockProperties.AGE_16);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockReeds() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockReeds(BlockState blockstate) {
        super(blockstate);
    }
}