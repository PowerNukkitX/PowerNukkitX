package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TradeRecipeBuildUtils;
import cn.nukkit.utils.Utils;

import java.util.Random;

public class ProfessionFletcher extends Profession {

    public ProfessionFletcher() {
        super(4, BlockID.FLETCHING_TABLE, "entity.villager.fletcher", Sound.DIG_WOOD);
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);

        Item bow = Item.get(Item.BOW);
        int[] bowEnchantments = new int[]{Enchantment.ID_BOW_FLAME, Enchantment.ID_BOW_INFINITY, Enchantment.ID_BOW_KNOCKBACK, Enchantment.ID_BOW_POWER, Enchantment.ID_DURABILITY};
        Enchantment bowEnchantmemt = Enchantment.getEnchantment(bowEnchantments[random.nextInt(bowEnchantments.length)]);
        bowEnchantmemt.setLevel(random.nextInt(bowEnchantmemt.getMaxLevel()) + 1);
        bow.addEnchantment(bowEnchantmemt);

        Item crossbow = Item.get(Item.CROSSBOW);
        int[] crossbowEnchantments = new int[]{Enchantment.ID_CROSSBOW_MULTISHOT, Enchantment.ID_CROSSBOW_PIERCING, Enchantment.ID_CROSSBOW_QUICK_CHARGE, Enchantment.ID_DURABILITY};
        Enchantment crossbowEnchantment = Enchantment.getEnchantment(crossbowEnchantments[random.nextInt(crossbowEnchantments.length)]);
        crossbowEnchantment.setLevel(random.nextInt(crossbowEnchantment.getMaxLevel()) + 1);
        crossbow.addEnchantment(crossbowEnchantment);

        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.STICK, 0, 32), Item.get(Item.EMERALD))
                .setMaxUses(16)
                .setRewardExp((byte) 1)
                .setTier(1)
                .setTraderExp(2)
                .setPriceMultiplierA(0.05f)
                .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.ARROW, 0, 16))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(1)
                    .setTraderExp(1)
                    .setPriceMultiplierA(0.05f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(BlockID.GRAVEL, 0, 10), Item.get(Item.FLINT, 0, 10))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(1)
                    .setTraderExp(1)
                    .setPriceMultiplierA(0.05f)
                    .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.FLINT, 0, 26), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 2), Item.get(Item.BOW))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.STRING, 0, 14), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), Item.get(Item.CROSSBOW))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.FEATHER, 0, 24), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 7 + random.nextInt(15)), bow)
                        .setMaxUses(3)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(BlockID.TRIPWIRE_HOOK, 0, 8), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(30)
                        .setPriceMultiplierA(0.05f)
                        .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 8 + random.nextInt(15)), crossbow)
                    .setMaxUses(3)
                    .setRewardExp((byte) 1)
                    .setTier(5)
                    .setTraderExp(15)
                    .setPriceMultiplierA(0.05f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 2), Item.get(Item.ARROW, 0, 5), Item.get(Item.ARROW, Utils.rand(6, 43), 5))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(5)
                    .setTraderExp(30)
                    .setPriceMultiplierA(0.05f)
                    .build());
        }
        return recipes;
    }

}
