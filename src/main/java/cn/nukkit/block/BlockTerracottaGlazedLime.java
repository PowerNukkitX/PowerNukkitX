package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockTerracottaGlazedLime extends BlockGlazedTerracotta {

    public BlockTerracottaGlazedLime() {
        this(0);
    }

    public BlockTerracottaGlazedLime(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return LIME_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Lime Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}
