package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockChainCommandBlock extends BlockCommandBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHAIN_COMMAND_BLOCK, CommonBlockProperties.CONDITIONAL_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChainCommandBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChainCommandBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Chain Command Block";
    }
}