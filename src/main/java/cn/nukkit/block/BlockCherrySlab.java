package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCherrySlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cherry_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherrySlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherrySlab(BlockState blockstate) {
        super(blockstate);
    }
}