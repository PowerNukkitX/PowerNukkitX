package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHardStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:hard_stained_glass", CommonBlockProperties.COLOR);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHardStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHardStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}