package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemDiamond;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockDiamondOre extends BlockOre {
    public static final BlockProperties PROPERTIES = new BlockProperties(DIAMOND_ORE);
    public static final BlockDefinition DEFINITION = BlockOre.DEFINITION.toBuilder()
            .toolTier(ItemTool.TIER_IRON)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDiamondOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDiamondOre(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    protected @Nullable String getRawMaterial() {
        return ItemID.DIAMOND;
    }

    @Override
    public String getName() {
        return "Diamond Ore";
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
                    new ItemDiamond(0, count)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(3, 8);
    }
}