package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRepeatingCommandBlock extends BlockCommandBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(REPEATING_COMMAND_BLOCK, CommonBlockProperties.CONDITIONAL_BIT, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRepeatingCommandBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRepeatingCommandBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Repeating Command Block";
    }
}