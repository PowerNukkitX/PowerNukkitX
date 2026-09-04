package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BlockSculk extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(SCULK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.2)
            .resistance(0.2)
            .toolType(ItemTool.TYPE_HOE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .dropExp(1)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSculk() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSculk(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }
    @Override
    public String getName() {
        return "Sculk";
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

    
    }
