package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemQuartz;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockQuartzOre extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(QUARTZ_ORE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3)
            .resistance(5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canSilkTouch(true)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockQuartzOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockQuartzOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Quartz Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            int count = 1;
            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                int i = ThreadLocalRandom.current().nextInt(fortune.getLevel() + 2) - 1;

                if (i < 0) {
                    i = 0;
                }

                count = i + 1;
            }

            return new Item[]{
                    new ItemQuartz(0, count)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public int getDropExp() {
        return new NukkitRandom().nextInt(1, 5);
    }

    
    }