package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
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