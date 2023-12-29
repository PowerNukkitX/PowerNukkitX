package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockTerracottaGlazedPurple extends BlockGlazedTerracotta {

    public BlockTerracottaGlazedPurple() {
        this(0);
    }

    public BlockTerracottaGlazedPurple(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return PURPLE_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Purple Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
