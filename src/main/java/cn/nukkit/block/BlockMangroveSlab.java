package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveSlab(BlockState blockstate) {
        super(blockstate);
    }
}