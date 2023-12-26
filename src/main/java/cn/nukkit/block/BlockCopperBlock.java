package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCopperBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:copper_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperBlock(BlockState blockstate) {
        super(blockstate);
    }
}