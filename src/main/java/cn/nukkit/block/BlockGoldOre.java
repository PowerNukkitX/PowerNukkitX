package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGoldOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gold_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
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

    @Nullable
    @Override
    protected String getRawMaterial() {
        return ItemID.RAW_GOLD;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }
}