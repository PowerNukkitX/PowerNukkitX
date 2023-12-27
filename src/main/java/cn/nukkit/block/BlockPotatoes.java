package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPotatoes extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:potatoes", CommonBlockProperties.GROWTH);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPotatoes() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPotatoes(BlockState blockstate) {
        super(blockstate);
    }
}