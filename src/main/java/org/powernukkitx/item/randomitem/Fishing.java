package org.powernukkitx.item.randomitem;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.randomitem.fishing.FishingEnchantmentItemSelector;
import org.powernukkitx.math.NukkitMath;

import static org.powernukkitx.item.randomitem.RandomItem.ROOT;
import static org.powernukkitx.item.randomitem.RandomItem.putSelector;
import static org.powernukkitx.item.randomitem.RandomItem.selectFrom;

/**
 * @author Snake1999
 * @since 2016/1/15
 */
public final class Fishing {
    public static final Selector ROOT_FISHING = putSelector(new Selector(ROOT));
    public static final Selector FISHES = putSelector(new Selector(ROOT_FISHING), 0.85F);
    public static final Selector TREASURES = putSelector(new Selector(ROOT_FISHING), 0.05F);
    public static final Selector JUNKS = putSelector(new Selector(ROOT_FISHING), 0.1F);
    public static final Selector FISH = putSelector(new ConstantItemSelector(ItemID.COD, FISHES), 0.6F);
    public static final Selector SALMON = putSelector(new ConstantItemSelector(ItemID.SALMON, FISHES), 0.25F);
    public static final Selector TROPICAL_FISH = putSelector(new ConstantItemSelector(ItemID.TROPICAL_FISH, FISHES), 0.02F);
    public static final Selector PUFFERFISH = putSelector(new ConstantItemSelector(ItemID.PUFFERFISH, FISHES), 0.13F);
    // treasure weights out of 31: 5 each, except the book which is 6
    public static final Selector TREASURE_NAUTILUS_SHELL = putSelector(new ConstantItemSelector(ItemID.NAUTILUS_SHELL, TREASURES), 0.1613F);
    public static final Selector TREASURE_NAME_TAG = putSelector(new ConstantItemSelector(ItemID.NAME_TAG, TREASURES), 0.1613F);
    public static final Selector TREASURE_SADDLE = putSelector(new ConstantItemSelector(ItemID.SADDLE, TREASURES), 0.1613F);
    public static final Selector TREASURE_BOW = putSelector(new ConstantItemSelector(ItemID.BOW, TREASURES), 0.1613F);
    public static final Selector TREASURE_FISHING_ROD = putSelector(new ConstantItemSelector(ItemID.FISHING_ROD, TREASURES), 0.1613F);
    public static final Selector TREASURE_ENCHANTED_BOOK = putSelector(new FishingEnchantmentItemSelector(ItemID.ENCHANTED_BOOK, TREASURES),  0.1935F);
    // junk weights out of 101
    public static final Selector JUNK_WATERLILY = putSelector(new ConstantItemSelector(Item.get(BlockID.WATERLILY), JUNKS), 0.1683F);
    public static final Selector JUNK_BOWL = putSelector(new ConstantItemSelector(ItemID.BOWL, JUNKS), 0.099F);
    public static final Selector JUNK_FISHING_ROD = putSelector(new ConstantItemSelector(ItemID.FISHING_ROD, JUNKS), 0.0198F);
    public static final Selector JUNK_LEATHER = putSelector(new ConstantItemSelector(ItemID.LEATHER, JUNKS), 0.099F);
    public static final Selector JUNK_LEATHER_BOOTS = putSelector(new ConstantItemSelector(ItemID.LEATHER_BOOTS, JUNKS), 0.099F);
    public static final Selector JUNK_ROTTEN_FLESH = putSelector(new ConstantItemSelector(ItemID.ROTTEN_FLESH, JUNKS), 0.099F);
    public static final Selector JUNK_STICK = putSelector(new ConstantItemSelector(ItemID.STICK, JUNKS), 0.0495F);
    public static final Selector JUNK_STRING_ITEM = putSelector(new ConstantItemSelector(ItemID.STRING, JUNKS), 0.0495F);
    public static final Selector JUNK_WATTER_BOTTLE = putSelector(new ConstantItemSelector(ItemID.POTION, 0, JUNKS), 0.099F);
    public static final Selector JUNK_BONE = putSelector(new ConstantItemSelector(ItemID.BONE, JUNKS), 0.099F);
    public static final Selector JUNK_TRIPWIRE_HOOK = putSelector(new ConstantItemSelector(Item.get(BlockID.TRIPWIRE_HOOK), JUNKS), 0.099F);
    public static final Selector JUNK_INK_SAC = putSelector(new ConstantItemSelector(ItemID.INK_SAC, JUNKS), 0.0099F);
    public static final Selector JUNK_INK_SAC_STACK = putSelector(new ConstantItemSelector(ItemID.INK_SAC, 0, 10, JUNKS), 0.0099F);

    /**
     * Selects a fish the way the {@code fishing/fish} loot table does, without the
     * treasure and junk categories that {@link #getFishingResult(Item)} can also return.
     *
     * @return a cod, a salmon, a pufferfish or a tropical fish
     */
    public static Item getRandomFish() {
        return (Item) selectFrom(FISHES);
    }

    public static Item getFishingResult(Item rod) {
        int fortuneLevel = 0;
        int lureLevel = 0;
        if (rod != null) {
            fortuneLevel = rod.getEnchantmentLevel(Enchantment.ID_FORTUNE_FISHING);
            lureLevel = rod.getEnchantmentLevel(Enchantment.ID_LURE);
        }
        return getFishingResult(fortuneLevel, lureLevel);
    }

    public static Item getFishingResult(int fortuneLevel, int lureLevel) {
        float treasureChance = NukkitMath.clamp(0.05f + 0.01f * fortuneLevel - 0.01f * lureLevel, 0, 1);
        float junkChance = NukkitMath.clamp(0.05f - 0.025f * fortuneLevel - 0.01f * lureLevel, 0, 1);
        float fishChance = NukkitMath.clamp(1 - treasureChance - junkChance, 0, 1);
        putSelector(FISHES, fishChance);
        putSelector(TREASURES, treasureChance);
        putSelector(JUNKS, junkChance);
        Object result = selectFrom(ROOT_FISHING);
        return (Item) result;
    }
}
