package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHayBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:hay_block", CommonBlockProperties.DEPRECATED, CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHayBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHayBlock(BlockState blockstate) {
        super(blockstate);
    }
}