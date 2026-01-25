package cn.nukkit.utils;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class TradeRecipeBuildUtils {
    private final CompoundTag recipe;
    private final int size;

    public static final int TRADE_RECIPEID = 10000;
    public static final ConcurrentHashMap<Integer, CompoundTag> RECIPE_MAP = new ConcurrentHashMap<>();
    private static final AtomicInteger TRADE_RECIPE_NETID = new AtomicInteger(TRADE_RECIPEID);

    public TradeRecipeBuildUtils(CompoundTag tag, int size) {
        this.recipe = tag;
        this.size = size;
    }

    /**
     * @param buyA   Transaction materials 1
     * @param output Transaction results
     * @return recipe builder
     */
    public static TradeRecipeBuildUtils of(Item buyA, Item output) {
        var cmp = new CompoundTag()
                .putCompound("buyA", new CompoundTag()
                        .putByte("Count", buyA.getCount())
                        .putShort("Damage", buyA.getDamage())
                        .putString("Name", buyA.getId())
                        .putBoolean("WasPickedUp", false)) // Is it a dropped item?
                .putInt("buyCountA", buyA.getCount())
                .putInt("buyCountB", 0)
                .putInt("demand", 0) // Unknown
                .putInt("netId", TRADE_RECIPE_NETID.getAndIncrement())
                .putFloat("priceMultiplierB", 0)
                .putCompound("sell", new CompoundTag()
                        .putByte("Count", output.getCount())
                        .putShort("Damage", output.getDamage())
                        .putString("Name", output.getId())
                        .putBoolean("WasPickedUp", false))
                .putInt("uses", 0); // Unknown
        if (buyA.hasCompoundTag()) {
            cmp.getCompound("buyA").putCompound("tag", buyA.getNamedTag());
        }
        if (output.hasCompoundTag()) {
            cmp.getCompound("sell").putCompound("tag", output.getNamedTag());
        }
        return new TradeRecipeBuildUtils(cmp, 2);
    }

    /**
     * @param buyA   Transaction materials 1
     * @param buyB   Transaction materials 2
     * @param output Transaction results
     * @return Recipe builder
     */
    public static TradeRecipeBuildUtils of(Item buyA, Item buyB, Item output) {
        var cmp = new CompoundTag()
                .putCompound("buyA", new CompoundTag()
                        .putByte("Count", buyA.getCount())
                        .putShort("Damage", buyA.getDamage())
                        .putString("Name", buyA.getId())
                        .putBoolean("WasPickedUp", false)) // Is it a dropped item?
                .putCompound("buyB", new CompoundTag()
                        .putByte("Count", buyB.getCount())
                        .putShort("Damage", buyB.getDamage())
                        .putString("Name", buyB.getId())
                        .putBoolean("WasPickedUp", false))
                .putInt("buyCountA", buyA.getCount())
                .putInt("buyCountA", buyB.getCount())
                .putInt("demand", 0) // Unknown
                .putInt("netId", TRADE_RECIPE_NETID.getAndIncrement())
                .putCompound("sell", new CompoundTag()
                        .putByte("Count", output.getCount())
                        .putShort("Damage", output.getDamage())
                        .putString("Name", output.getId())
                        .putBoolean("WasPickedUp", false))
                .putInt("uses", 0); // Unknown
        if (buyA.hasCompoundTag()) {
            cmp.getCompound("buyA").putCompound("tag", buyA.getNamedTag());
        }
        if (buyB.hasCompoundTag()) {
            cmp.getCompound("buyB").putCompound("tag", buyB.getNamedTag());
        }
        if (output.hasCompoundTag()) {
            cmp.getCompound("sell").putCompound("tag", output.getNamedTag());
        }
        return new TradeRecipeBuildUtils(cmp, 3);
    }

    /**
     * @param maxUses Set the maximum number of transactions for this trading recipe
     */
    public TradeRecipeBuildUtils setMaxUses(int maxUses) {
        recipe.putInt("maxUses", maxUses); // Maximum number of transactions
        return this;
    }

    /**
     * @param priceMultiplierA Set the price increase factor for this trade recipe (first traded item)
     */
    public TradeRecipeBuildUtils setPriceMultiplierA(float priceMultiplierA) {
        recipe.putFloat("priceMultiplierA", priceMultiplierA); // Price increase?
        return this;
    }

    /**
     * @param priceMultiplierB Set the price increase factor for this trade recipe (second trade item)
     */
    public TradeRecipeBuildUtils setPriceMultiplierB(float priceMultiplierB) {
        if (size == 3) {
            recipe.putFloat("priceMultiplierB", priceMultiplierB); // Price increase?
            return this;
        }
        return this;
    }

    /**
     * @param rewardExp Set the experience points awarded to players for this transaction recipe.
     */
    public TradeRecipeBuildUtils setRewardExp(Byte rewardExp) {
        recipe.putByte("rewardExp", rewardExp);
        return this;
    }

    /**
     * @param tier The transaction level required for this recipe starts at 1.
     */
    public TradeRecipeBuildUtils setTier(int tier) {
        recipe.putInt("tier", --tier); // Village resident level required
        return this;
    }

    /**
     * @param traderExp Set the experience granted to villagers by this trade recipe.
     */
    public TradeRecipeBuildUtils setTraderExp(int traderExp) {
        recipe.putInt("traderExp", traderExp); // Experience gained by villagers
        return this;
    }

    public CompoundTag build() {
        RECIPE_MAP.put(recipe.getInt("netId"), recipe);
        return recipe;
    }
}
