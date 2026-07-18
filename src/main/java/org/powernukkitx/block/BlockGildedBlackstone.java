package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockGildedBlackstone extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(GILDED_BLACKSTONE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGildedBlackstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGildedBlackstone(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Gilded Blackstone";
    }

    
    @Override
    public Item[] getDrops(Item item) {
        if (!item.isPickaxe() || item.getTier() < ItemTool.TIER_WOODEN) {
            return Item.EMPTY_ARRAY;
        }

        int dropOdds;
        int fortune = 0;
        Enchantment enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        if (enchantment != null) {
            fortune = enchantment.getLevel();
        }

        dropOdds = switch (fortune) {
            case 0 -> 10;
            case 1 -> 7;
            case 2 -> 4;
            default -> 1;
        };

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (dropOdds > 1 && random.nextInt(dropOdds) != 0) {
            return new Item[] { toItem() };
        }

        return new Item[] { Item.get(ItemID.GOLD_NUGGET, 0, random.nextInt(2, 6)) };
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    
    }