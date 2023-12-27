package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGlowFrame extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:glow_frame", CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.ITEM_FRAME_MAP_BIT, CommonBlockProperties.ITEM_FRAME_PHOTO_BIT);

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