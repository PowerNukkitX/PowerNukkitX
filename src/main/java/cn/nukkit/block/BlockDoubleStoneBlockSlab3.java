package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDoubleStoneBlockSlab3 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:double_stone_block_slab3", CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_3);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDoubleStoneBlockSlab3() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDoubleStoneBlockSlab3(BlockState blockstate) {
        super(blockstate);
    }
}