package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.math.NukkitMath;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author joserobjr
 * @since 2021-06-13
 */


public abstract class BlockOre extends BlockSolid {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3)
            .resistance(3)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_STONE)
            .canSilkTouch(true)
            .build();

    public BlockOre(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockOre(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!canHarvest(item)) {
            return Item.EMPTY_ARRAY;
        }

        String rawMaterial = getRawMaterial();
        if (rawMaterial == null) {
            return super.getDrops(item);
        }

        float multiplier = getDropMultiplier();
        int amount = (int) multiplier;
        if (amount > 1) {
            amount = 1 + ThreadLocalRandom.current().nextInt(amount);
        }

        int fortuneLevel = NukkitMath.clamp(item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING), 0, 3);
        if (fortuneLevel > 0) {
            int increase = ThreadLocalRandom.current().nextInt((int)(multiplier * fortuneLevel) + 1);
            amount += increase;
        }

        Item itemRaw = Item.get(rawMaterial);
        itemRaw.setCount(amount);
        return new Item[]{itemRaw};
    }

    protected @Nullable abstract String getRawMaterial();

    protected float getDropMultiplier() {
        return 1;
    }
}
