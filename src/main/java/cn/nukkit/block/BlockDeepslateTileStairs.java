package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateTileStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_tile_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateTileStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateTileStairs(BlockState blockstate) {
        super(blockstate);
    }
}