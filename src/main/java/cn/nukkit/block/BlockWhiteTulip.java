package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteTulip extends BlockFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(WHITE_TULIP);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteTulip() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockWhiteTulip(BlockState blockstate) {
        super(blockstate);
    }
}