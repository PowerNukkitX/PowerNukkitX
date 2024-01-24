package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCoalOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(COAL_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoalOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoalOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Coal Ore";
    }

    @Override
    protected @Nullable String getRawMaterial() {
        return ItemID.COAL;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(3);
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 3;
    }
}