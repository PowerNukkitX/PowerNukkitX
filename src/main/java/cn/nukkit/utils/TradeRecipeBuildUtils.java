package cn.nukkit.utils;

import cn.nukkit.item.Item;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class TradeRecipeBuildUtils {
    private NbtMap recipe;
    private final int size;

    public static final int TRADE_RECIPEID = 10000;
    public static final ConcurrentHashMap<Integer, NbtMap> RECIPE_MAP = new ConcurrentHashMap<>();
    private static final AtomicInteger TRADE_RECIPE_NETID = new AtomicInteger(TRADE_RECIPEID);

    public TradeRecipeBuildUtils(NbtMap tag, int size) {
        this.recipe = tag;
        this.size = size;
    }

    /**
     * @param buyA   Transaction materials 1
     * @param output Transaction results
     * @return recipe builder
     */
    public static TradeRecipeBuildUtils of(Item buyA, Item output) {
        final NbtMapBuilder buyABuilder = NbtMap.builder()
                .putByte("Count", (byte) buyA.getCount())
                .putShort("Damage", (short) buyA.getDamage())
                .putString("Name", buyA.getId())
                .putBoolean("WasPickedUp", false);  // Is it a dropped item?
        if (buyA.hasCompoundTag()) {
            buyABuilder.putCompound("tag", buyA.getNamedTag());
        }
        final NbtMapBuilder outputBuilder = NbtMap.builder()
                .putByte("Count", (byte) output.getCount())
                .putShort("Damage", (short) output.getDamage())
                .putString("Name", output.getId())
                .putBoolean("WasPickedUp", false);
        if (output.hasCompoundTag()) {
            outputBuilder.putCompound("tag", output.getNamedTag());
        }
        var cmp = NbtMap.builder()
                .putCompound("buyA", buyABuilder.build())
                .putInt("buyCountA", buyA.getCount())
                .putInt("buyCountB", 0)
                .putInt("demand", 0) // Unknown
                .putInt("netId", TRADE_RECIPE_NETID.getAndIncrement())
                .putFloat("priceMultiplierB", 0)
                .putCompound("sell", outputBuilder.build())
                .putInt("uses", 0); // Unknown
        return new TradeRecipeBuildUtils(cmp.build(), 2);
    }

    /**
     * @param buyA   Transaction materials 1
     * @param buyB   Transaction materials 2
     * @param output Transaction results
     * @return Recipe builder
     */
    public static TradeRecipeBuildUtils of(Item buyA, Item buyB, Item output) {
        final NbtMapBuilder buyABuilder = NbtMap.builder()
                .putByte("Count", (byte) buyA.getCount())
                .putShort("Damage", (short) buyA.getDamage())
                .putString("Name", buyA.getId())
                .putBoolean("WasPickedUp", false);
        if (buyA.hasCompoundTag()) {
            buyABuilder.putCompound("tag", buyA.getNamedTag());
        }
        final NbtMapBuilder buyBBuilder = NbtMap.builder()
                .putByte("Count", (byte) buyB.getCount())
                .putShort("Damage", (short) buyB.getDamage())
                .putString("Name", buyB.getId())
                .putBoolean("WasPickedUp", false);
        if (buyB.hasCompoundTag()) {
            buyBBuilder.putCompound("tag", buyB.getNamedTag());
        }
        final NbtMapBuilder outputBuilder = NbtMap.builder()
                .putByte("Count", (byte) output.getCount())
                .putShort("Damage", (short) output.getDamage())
                .putString("Name", output.getId())
                .putBoolean("WasPickedUp", false);
        if (output.hasCompoundTag()) {
            outputBuilder.putCompound("tag", output.getNamedTag());
        }
        var cmp = NbtMap.builder()
                .putCompound("buyA", buyABuilder.build())
                .putCompound("buyB", buyBBuilder.build())
                .putInt("buyCountA", buyA.getCount())
                .putInt("buyCountA", buyB.getCount())
                .putInt("demand", 0) // Unknown
                .putInt("netId", TRADE_RECIPE_NETID.getAndIncrement())
                .putCompound("sell", outputBuilder.build())
                .putInt("uses", 0); // Unknown
        return new TradeRecipeBuildUtils(cmp.build(), 3);
    }

    /**
     * @param maxUses Set the maximum number of transactions for this trading recipe
     */
    public TradeRecipeBuildUtils setMaxUses(int maxUses) {
        this.recipe = recipe.toBuilder().putInt("maxUses", maxUses).build(); // Maximum number of transactions
        return this;
    }

    /**
     * @param priceMultiplierA Set the price increase factor for this trade recipe (first traded item)
     */
    public TradeRecipeBuildUtils setPriceMultiplierA(float priceMultiplierA) {
        this.recipe = recipe.toBuilder().putFloat("priceMultiplierA", priceMultiplierA).build(); // Price increase?
        return this;
    }

    /**
     * @param priceMultiplierB Set the price increase factor for this trade recipe (second trade item)
     */
    public TradeRecipeBuildUtils setPriceMultiplierB(float priceMultiplierB) {
        if (size == 3) {
            this.recipe = recipe.toBuilder().putFloat("priceMultiplierB", priceMultiplierB).build(); // Price increase?
            return this;
        }
        return this;
    }

    /**
     * @param rewardExp Set the experience points awarded to players for this transaction recipe.
     */
    public TradeRecipeBuildUtils setRewardExp(Byte rewardExp) {
        this.recipe = recipe.toBuilder().putByte("rewardExp", rewardExp).build();
        return this;
    }

    /**
     * @param tier The transaction level required for this recipe starts at 1.
     */
    public TradeRecipeBuildUtils setTier(int tier) {
        this.recipe = recipe.toBuilder().putInt("tier", --tier).build(); // Village resident level required
        return this;
    }

    /**
     * @param traderExp Set the experience granted to villagers by this trade recipe.
     */
    public TradeRecipeBuildUtils setTraderExp(int traderExp) {
        this.recipe = recipe.toBuilder().putInt("traderExp", traderExp).build(); // Experience gained by villagers
        return this;
    }

    public NbtMap build() {
        RECIPE_MAP.put(recipe.getInt("netId"), recipe);
        return recipe;
    }
}
