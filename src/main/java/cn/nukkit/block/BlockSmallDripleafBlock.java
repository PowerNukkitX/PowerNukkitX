package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmallDripleafBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:small_dripleaf_block", CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmallDripleafBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmallDripleafBlock(BlockState blockstate) {
        super(blockstate);
    }
}