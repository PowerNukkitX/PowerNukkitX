package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.NukkitMath;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author joserobjr
 * @since 2021-06-13
 */
@PowerNukkitOnly
@Since("FUTURE")
public abstract class BlockOre extends BlockSolid {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockOre() {
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!canHarvest(item)) {
            return Item.EMPTY_ARRAY;
        }
        MinecraftItemID rawMaterial = getRawMaterial();
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
        return new Item[]{ rawMaterial.get(amount) };
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @Nullable
    protected abstract MinecraftItemID getRawMaterial();

    @PowerNukkitOnly
    @Since("FUTURE")
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
