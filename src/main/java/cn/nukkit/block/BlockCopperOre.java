package cn.nukkit.block;

import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockCopperOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:copper_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Copper Ore";
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Nullable
    @Override
    protected String getRawMaterial() {
        return ItemID.RAW_COPPER;
    }

    @Override
    protected float getDropMultiplier() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 3;
    }
}