package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGlowFrame extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(GLOW_FRAME, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.ITEM_FRAME_MAP_BIT, CommonBlockProperties.ITEM_FRAME_PHOTO_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGlowFrame() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGlowFrame(BlockState blockstate) {
        super(blockstate);
    }
}