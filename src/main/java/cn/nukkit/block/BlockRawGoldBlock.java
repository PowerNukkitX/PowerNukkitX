package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockRawGoldBlock extends BlockRaw {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:raw_gold_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRawGoldBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRawGoldBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Block of Raw Gold";
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }
}