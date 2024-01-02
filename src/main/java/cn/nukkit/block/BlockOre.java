package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.NukkitMath;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author joserobjr
 * @since 2021-06-13
 */


public abstract class BlockOre extends BlockSolid {

    public BlockOre(BlockState blockState) {
        super(blockState);
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

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
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
