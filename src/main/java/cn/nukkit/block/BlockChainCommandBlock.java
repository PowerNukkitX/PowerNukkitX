package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockChainCommandBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:chain_command_block", CommonBlockProperties.CONDITIONAL_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChainCommandBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChainCommandBlock(BlockState blockstate) {
        super(blockstate);
    }
}