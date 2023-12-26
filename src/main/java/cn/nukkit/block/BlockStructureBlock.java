package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStructureBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:structure_block", CommonBlockProperties.STRUCTURE_BLOCK_TYPE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStructureBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStructureBlock(BlockState blockstate) {
        super(blockstate);
    }
}