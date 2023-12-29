package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockTerracottaGlazedBrown extends BlockGlazedTerracotta {

    public BlockTerracottaGlazedBrown() {
        this(0);
    }

    public BlockTerracottaGlazedBrown(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return BROWN_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Brown Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
