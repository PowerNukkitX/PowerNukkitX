package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockTerracottaGlazedGreen extends BlockGlazedTerracotta {

    public BlockTerracottaGlazedGreen() {
        this(0);
    }

    public BlockTerracottaGlazedGreen(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return GREEN_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Green Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
