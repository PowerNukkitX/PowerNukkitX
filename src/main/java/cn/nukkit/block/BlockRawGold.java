package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * @author LoboMetalurgico
 * @since 08/06/2021
 */


public class BlockRawGold extends BlockRaw {


    public BlockRawGold() {
        this(0);
    }


    public BlockRawGold(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Block of Raw Gold";
    }

    @Override
    public int getId() {
        return RAW_GOLD_BLOCK;
    }


    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }
}
