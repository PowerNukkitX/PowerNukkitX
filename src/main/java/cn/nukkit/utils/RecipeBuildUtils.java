package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

@PowerNukkitXOnly
@Since("1.19.21-r7")
public class RecipeBuildUtils {
    private final CompoundTag recipe;
    private final int size;

    private static int netId = 3000;

    public RecipeBuildUtils(CompoundTag tag, int size) {
        this.recipe = tag;
        this.size = size;
    }

    /**
     * @param buyA   交易材料1
     * @param output 交易结果
     * @return 配方构造工具
     */
    public static RecipeBuildUtils of(Item buyA, Item output) {
        var cmp = new CompoundTag()
                .putCompound("buyA", new CompoundTag()
                        .putByte("Count", buyA.getCount())
                        .putShort("Damage", buyA.getDamage())
                        .putString("Name", buyA.getNamespaceId())
                        .putBoolean("WasPickedUp", false))//是否是掉落物？？
                .putInt("buyCountA", buyA.getCount())
                .putInt("buyCountB", 0)
                .putInt("demand", 0)//未知
                .putInt("netId", netId++)
                .putFloat("priceMultiplierB", 0)
                .putCompound("sell", new CompoundTag()
                        .putByte("Count", output.getCount())
                        .putShort("Damage", output.getDamage())
                        .putString("Name", output.getNamespaceId())
                        .putBoolean("WasPickedUp", false))
                .putInt("uses", 0);//未知
        if (buyA.hasCompoundTag()) {
            cmp.getCompound("buyA").putCompound("tag", buyA.getNamedTag());
        }
        if (output.hasCompoundTag()) {
            cmp.getCompound("sell").putCompound("tag", output.getNamedTag());
        }
        return new RecipeBuildUtils(cmp, 2);
    }

    /**
     * @param buyA   交易材料1
     * @param buyB   交易材料2
     * @param output 交易结果
     * @return 配方构造工具
     */
    public static RecipeBuildUtils of(Item buyA, Item buyB, Item output) {
        var cmp = new CompoundTag()
                .putCompound("buyA", new CompoundTag()
                        .putByte("Count", buyA.getCount())
                        .putShort("Damage", buyA.getDamage())
                        .putString("Name", buyA.getNamespaceId())
                        .putBoolean("WasPickedUp", false))//是否是掉落物？？
                .putCompound("buyB", new CompoundTag()
                        .putByte("Count", buyB.getCount())
                        .putShort("Damage", buyB.getDamage())
                        .putString("Name", buyB.getNamespaceId())
                        .putBoolean("WasPickedUp", false))
                .putInt("buyCountA", buyA.getCount())
                .putInt("buyCountA", buyB.getCount())
                .putInt("demand", 0)//未知
                .putInt("netId", netId++)
                .putCompound("sell", new CompoundTag()
                        .putByte("Count", output.getCount())
                        .putShort("Damage", output.getDamage())
                        .putString("Name", output.getNamespaceId())
                        .putBoolean("WasPickedUp", false))
                .putInt("uses", 0);//未知
        if (buyA.hasCompoundTag()) {
            cmp.getCompound("buyA").putCompound("tag", buyA.getNamedTag());
        }
        if (buyB.hasCompoundTag()) {
            cmp.getCompound("buyB").putCompound("tag", buyB.getNamedTag());
        }
        if (output.hasCompoundTag()) {
            cmp.getCompound("sell").putCompound("tag", output.getNamedTag());
        }
        return new RecipeBuildUtils(cmp, 3);
    }

    /**
     * @param maxUses 设置该交易配方最大交易次数
     */
    public RecipeBuildUtils setMaxUses(int maxUses) {
        recipe.putInt("maxUses", maxUses);//最大交易次数
        return this;
    }

    /**
     * @param priceMultiplierA 设置该交易配方价格增长系数(第一个交易物品)
     */
    public RecipeBuildUtils setPriceMultiplierA(float priceMultiplierA) {
        recipe.putFloat("priceMultiplierA", priceMultiplierA);//价格增长？
        return this;
    }

    /**
     * @param priceMultiplierB 设置该交易配方价格增长系数(第二个交易物品)
     */
    public RecipeBuildUtils setPriceMultiplierB(float priceMultiplierB) {
        if (size == 3) {
            recipe.putFloat("priceMultiplierB", priceMultiplierB);//价格增长？
            return this;
        }
        return this;
    }

    /**
     * @param rewardExp 设置该交易配方奖励玩家的经验值
     */
    public RecipeBuildUtils setRewardExp(Byte rewardExp) {
        recipe.putByte("rewardExp", rewardExp);
        return this;
    }

    /**
     * @param tier 该配方需要的交易等级 从1开始
     */
    public RecipeBuildUtils setTier(int tier) {
        recipe.putInt("tier", --tier);//需要村民等级
        return this;
    }

    /**
     * @param traderExp 设置该交易配方给予村民的经验
     */
    public RecipeBuildUtils setTraderExp(int traderExp) {
        recipe.putInt("traderExp", traderExp);//村民增加的经验
        return this;
    }

    public CompoundTag build() {
        return recipe;
    }
}
