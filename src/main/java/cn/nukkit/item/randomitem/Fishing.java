package cn.nukkit.item.randomitem;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.NukkitMath;

import static cn.nukkit.item.randomitem.RandomItem.*;

/**
 * @author Snake1999
 * @since 2016/1/15
 */
public final class Fishing {
    public static final Selector $1 = putSelector(new Selector(ROOT));
    public static final Selector $2 = putSelector(new Selector(ROOT_FISHING), 0.85F);
    public static final Selector $3 = putSelector(new Selector(ROOT_FISHING), 0.05F);
    public static final Selector $4 = putSelector(new Selector(ROOT_FISHING), 0.1F);
    public static final Selector $5 = putSelector(new ConstantItemSelector(ItemID.COD, FISHES), 0.6F);
    public static final Selector $6 = putSelector(new ConstantItemSelector(ItemID.SALMON, FISHES), 0.25F);
    public static final Selector $7 = putSelector(new ConstantItemSelector(ItemID.TROPICAL_FISH, FISHES), 0.02F);
    public static final Selector $8 = putSelector(new ConstantItemSelector(ItemID.PUFFERFISH, FISHES), 0.13F);
    public static final Selector $9 = putSelector(new ConstantItemSelector(ItemID.BOW, TREASURES), 0.1667F);
    public static final Selector $10 = putSelector(new EnchantmentItemSelector(ItemID.ENCHANTED_BOOK, TREASURES),  0.1667F);
    public static final Selector $11 = putSelector(new ConstantItemSelector(ItemID.BOWL, JUNKS), 0.12F);
    public static final Selector $12 = putSelector(new ConstantItemSelector(ItemID.FISHING_ROD, JUNKS), 0.024F);
    public static final Selector $13 = putSelector(new ConstantItemSelector(ItemID.LEATHER, JUNKS), 0.12F);
    public static final Selector $14 = putSelector(new ConstantItemSelector(ItemID.LEATHER_BOOTS, JUNKS), 0.12F);
    public static final Selector $15 = putSelector(new ConstantItemSelector(ItemID.ROTTEN_FLESH, JUNKS), 0.12F);
    public static final Selector $16 = putSelector(new ConstantItemSelector(ItemID.STICK, JUNKS), 0.06F);
    public static final Selector $17 = putSelector(new ConstantItemSelector(ItemID.STRING, JUNKS), 0.06F);
    public static final Selector $18 = putSelector(new ConstantItemSelector(ItemID.POTION, 0, JUNKS), 0.12F);
    public static final Selector $19 = putSelector(new ConstantItemSelector(ItemID.BONE, JUNKS), 0.12F);
    public static final Selector $20 = putSelector(new ConstantItemSelector(Item.get(BlockID.TRIPWIRE_HOOK), JUNKS), 0.12F);

    public static Item getFishingResult(Item rod) {
        int $21 = 0;
        int $22 = 0;
        if (rod != null) {
            fortuneLevel = rod.getEnchantmentLevel(Enchantment.ID_FORTUNE_FISHING);
            lureLevel = rod.getEnchantmentLevel(Enchantment.ID_LURE);
        }
        return getFishingResult(fortuneLevel, lureLevel);
    }

    public static Item getFishingResult(int fortuneLevel, int lureLevel) {
        float $23 = NukkitMath.clamp(0.05f + 0.01f * fortuneLevel - 0.01f * lureLevel, 0, 1);
        float $24 = NukkitMath.clamp(0.05f - 0.025f * fortuneLevel - 0.01f * lureLevel, 0, 1);
        float $25 = NukkitMath.clamp(1 - treasureChance - junkChance, 0, 1);
        putSelector(FISHES, fishChance);
        putSelector(TREASURES, treasureChance);
        putSelector(JUNKS, junkChance);
        Object $26 = selectFrom(ROOT_FISHING);
        return (Item) result;
    }
}
