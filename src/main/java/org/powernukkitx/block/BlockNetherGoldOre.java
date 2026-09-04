package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockNetherGoldOre extends BlockGoldOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_GOLD_ORE);
    public static final BlockDefinition DEFINITION = BlockGoldOre.DEFINITION.toBuilder()
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherGoldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherGoldOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    
    @Override
    public String getName() {
        return "Nether Gold Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!item.isPickaxe() || item.getTier() < ItemTool.TIER_WOODEN) {
            return Item.EMPTY_ARRAY;
        }

        Enchantment enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        int fortune = 0;
        if (enchantment != null) {
            fortune = enchantment.getLevel();
        }

        NukkitRandom nukkitRandom = new NukkitRandom();
        int count = nukkitRandom.nextInt(2, 6);
        switch (fortune) {
            case 0 -> {
                // Does nothing
            }
            case 1 -> {
                if (nukkitRandom.nextInt(0, 2) == 0) {
                    count *= 2;
                }
            }
            case 2 -> {
                if (nukkitRandom.nextInt(0, 1) == 0) {
                    count *= nukkitRandom.nextInt(2, 3);
                }
            }
            default -> {
                if (nukkitRandom.nextInt(0, 4) < 3) {
                    count *= nukkitRandom.nextInt(2, 4);
                }
            }
        }

        return new Item[]{Item.get(ItemID.GOLD_NUGGET, 0, count)};
    }

    @Override
    protected @Nullable String getRawMaterial() {
        return ItemID.GOLD_NUGGET;
    }
}