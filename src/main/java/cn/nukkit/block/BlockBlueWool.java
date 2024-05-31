package cn.nukkit.block;

import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BlockBlueWool extends BlockWool {
    public static final BlockProperties $1 = new BlockProperties(BLUE_WOOL, Set.of(BlockTags.PNX_WOOL));

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBlueWool() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBlueWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}