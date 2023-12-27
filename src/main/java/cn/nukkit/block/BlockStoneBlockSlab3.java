package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBlockSlab3 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stone_block_slab3", CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_3);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneBlockSlab3() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStoneBlockSlab3(BlockState blockstate) {
        super(blockstate);
    }
}