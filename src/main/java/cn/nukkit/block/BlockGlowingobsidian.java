package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGlowingobsidian extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:glowingobsidian");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGlowingobsidian() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGlowingobsidian(BlockState blockstate) {
        super(blockstate);
    }
}