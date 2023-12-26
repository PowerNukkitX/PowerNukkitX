package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveDoubleSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}