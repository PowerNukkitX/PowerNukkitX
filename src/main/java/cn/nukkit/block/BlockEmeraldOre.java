package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockEmeraldOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:emerald_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEmeraldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEmeraldOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Emerald Ore";
    }

    @Nullable
    @Override
    protected String getRawMaterial() {
        return ItemID.EMERALD;
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