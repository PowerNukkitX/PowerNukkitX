package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDoubleStoneBlockSlab2 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(DOUBLE_STONE_BLOCK_SLAB2, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_2);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDoubleStoneBlockSlab2() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDoubleStoneBlockSlab2(BlockState blockstate) {
        super(blockstate);
    }


}