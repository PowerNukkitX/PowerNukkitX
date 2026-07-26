package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BlockSculk extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(SCULK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSculk() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSculk(BlockState blockstate) {
        super(blockstate);
    }
    @Override
    public String getName() {
        return "Sculk";
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
        return 0.2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public double calculateBreakTime(@NotNull Item item, @Nullable Player player) {
        if (canHarvest(item)) {
            return super.calculateBreakTime(item, player);
        } else {
            return 1;
        }
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getDropExp() {
        return 1;
    }
}
