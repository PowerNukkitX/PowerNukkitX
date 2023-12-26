package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCrackedDeepslateBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cracked_deepslate_bricks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrackedDeepslateBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrackedDeepslateBricks(BlockState blockstate) {
        super(blockstate);
    }
}