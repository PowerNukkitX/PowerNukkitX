package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateTileSlab extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_tile_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateTileSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateTileSlab(BlockState blockstate) {
        super(blockstate);
    }
}