package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBlockSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stone_block_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneBlockSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStoneBlockSlab(BlockState blockstate) {
        super(blockstate);
    }
}