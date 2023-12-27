package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDoubleStoneBlockSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:double_stone_block_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDoubleStoneBlockSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDoubleStoneBlockSlab(BlockState blockstate) {
        super(blockstate);
    }
}