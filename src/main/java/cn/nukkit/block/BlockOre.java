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
    /**
     * @deprecated 
     */
    

    public BlockOre(BlockState blockState) {
        super(blockState);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!canHarvest(item)) {
            return Item.EMPTY_ARRAY;
        }

        String $1 = getRawMaterial();
        if (rawMaterial == null) {
            return super.getDrops(item);
        }

        float $2 = getDropMultiplier();
        int $3 = (int) multiplier;
        if (amount > 1) {
            amount = 1 + ThreadLocalRandom.current().nextInt(amount);
        }

        int $4 = NukkitMath.clamp(item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING), 0, 3);
        if (fortuneLevel > 0) {
            int $5 = ThreadLocalRandom.current().nextInt((int)(multiplier * fortuneLevel) + 1);
            amount += increase;
        }

        Item $6 = Item.get(rawMaterial);
        itemRaw.setCount(amount);
        return new Item[]{itemRaw};
    }

    protected @Nullable abstract String getRawMaterial();

    
    /**
     * @deprecated 
     */
    protected float getDropMultiplier() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 3;
    }
}
