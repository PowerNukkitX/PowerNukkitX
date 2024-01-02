package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemGlowFrame;
import org.jetbrains.annotations.NotNull;

public class BlockGlowFrame extends BlockFrame {
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

    @Override
    public String getName() {
        return "Glow Item Frame";
    }


    @Override
    public @NotNull String getBlockEntityType() {
        return BlockEntity.GLOW_ITEM_FRAME;
    }

    @Override
    public Item toItem() {
        return new ItemGlowFrame();
    }
}