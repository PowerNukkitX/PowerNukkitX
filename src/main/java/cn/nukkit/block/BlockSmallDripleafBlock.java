package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

public class BlockSmallDripleafBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMALL_DRIPLEAF_BLOCK, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.UPPER_BLOCK_BIT);

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