package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGoldOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(GOLD_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGoldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGoldOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Gold Ore";
    }

    @Override
    protected @Nullable String getRawMaterial() {
        return ItemID.RAW_GOLD;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }
}