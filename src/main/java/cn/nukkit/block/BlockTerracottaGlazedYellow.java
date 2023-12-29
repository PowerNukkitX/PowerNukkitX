package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockTerracottaGlazedYellow extends BlockGlazedTerracotta {

    public BlockTerracottaGlazedYellow() {
        this(0);
    }

    public BlockTerracottaGlazedYellow(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return YELLOW_GLAZED_TERRACOTTA;
    }

    @Override
    public String getName() {
        return "Yellow Glazed Terracotta";
    }

    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
