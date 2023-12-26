package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBlockSlab4 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stone_block_slab4", CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_4);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneBlockSlab4() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStoneBlockSlab4(BlockState blockstate) {
        super(blockstate);
    }
}