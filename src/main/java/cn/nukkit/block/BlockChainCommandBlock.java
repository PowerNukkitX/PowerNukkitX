package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockChainCommandBlock extends BlockCommandBlock {
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


    @Override
    public String getName() {
        return "Chain Command Block";
    }
}