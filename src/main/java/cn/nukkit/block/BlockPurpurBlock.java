package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockPurpurBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purpur_block", CommonBlockProperties.CHISEL_TYPE, CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpurBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpurBlock(BlockState blockstate) {
        super(blockstate);
    }
}