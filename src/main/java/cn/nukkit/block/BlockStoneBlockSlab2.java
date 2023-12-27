package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBlockSlab2 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stone_block_slab2", CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_2);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneBlockSlab2() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStoneBlockSlab2(BlockState blockstate) {
        super(blockstate);
    }
}