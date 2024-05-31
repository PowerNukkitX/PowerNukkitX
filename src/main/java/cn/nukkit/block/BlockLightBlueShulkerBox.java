package cn.nukkit.block;

import cn.nukkit.tags.BlockTags;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockLightBlueShulkerBox extends BlockUndyedShulkerBox {
    public static final BlockProperties $1 = new BlockProperties(LIGHT_BLUE_SHULKER_BOX, Set.of(BlockTags.PNX_SHULKERBOX));

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlueShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlueShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}