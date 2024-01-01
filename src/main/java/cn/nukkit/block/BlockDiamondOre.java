package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockDiamondOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DIAMOND_ORE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDiamondOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDiamondOre(BlockState blockstate) {
        super(blockstate);
    }

    @Nullable
    @Override
    protected String getRawMaterial() {
        return ItemID.DIAMOND;
    }

    @Override
    public String getName() {
        return "Diamond Ore";
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(3, 8);
    }
}