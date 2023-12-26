package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCrackedNetherBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cracked_nether_bricks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrackedNetherBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrackedNetherBricks(BlockState blockstate) {
        super(blockstate);
    }
}