package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDoubleStoneBlockSlab4 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:double_stone_block_slab4", CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_4);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDoubleStoneBlockSlab4() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDoubleStoneBlockSlab4(BlockState blockstate) {
        super(blockstate);
    }
}