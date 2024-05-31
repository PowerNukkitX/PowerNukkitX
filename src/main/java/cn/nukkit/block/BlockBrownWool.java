package cn.nukkit.block;

import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockBrownWool extends BlockWool {
    public static final BlockProperties $1 = new BlockProperties(BROWN_WOOL, Set.of(BlockTags.PNX_WOOL));

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownWool() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}