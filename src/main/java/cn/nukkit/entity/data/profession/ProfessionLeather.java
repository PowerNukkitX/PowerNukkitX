package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemLeatherBoots;
import cn.nukkit.item.ItemLeatherChestplate;
import cn.nukkit.item.ItemLeatherHelmet;
import cn.nukkit.item.ItemLeatherLeggings;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

public class ProfessionLeather extends Profession {

    public ProfessionLeather() {
        super(12, BlockID.CAULDRON, "entity.villager.leather", Sound.BUCKET_FILL_WATER);
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);

        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.LEATHER, 0, 6), Item.get(Item.EMERALD))
                .setMaxUses(16)
                .setRewardExp((byte) 1)
                .setTier(1)
                .setTraderExp(2)
                .setPriceMultiplierA(0.05f)
                .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 7), ((ItemLeatherChestplate) Item.get(Item.LEATHER_CHESTPLATE)).setColor(DyeColor.values()[random.nextInt(DyeColor.values().length)]))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(1)
                    .setTraderExp(1)
                    .setPriceMultiplierA(0.2f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), ((ItemLeatherLeggings) Item.get(Item.LEATHER_LEGGINGS)).setColor(DyeColor.values()[random.nextInt(DyeColor.values().length)]))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(1)
                    .setTraderExp(1)
                    .setPriceMultiplierA(0.2f)
                    .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.FLINT, 0, 26), Item.get(Item.EMERALD))
                .setMaxUses(12)
                .setRewardExp((byte) 1)
                .setTier(2)
                .setTraderExp(10)
                .setPriceMultiplierA(0.05f)
                .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 5), ((ItemLeatherHelmet) Item.get(Item.LEATHER_HELMET)).setColor(DyeColor.values()[random.nextInt(DyeColor.values().length)]))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(2)
                    .setTraderExp(5)
                    .setPriceMultiplierA(0.2f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 4), ((ItemLeatherBoots) Item.get(Item.LEATHER_BOOTS)).setColor(DyeColor.values()[random.nextInt(DyeColor.values().length)]))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(2)
                    .setTraderExp(5)
                    .setPriceMultiplierA(0.2f)
                    .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.RABBIT_HIDE, 0, 9), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 7), ((ItemLeatherChestplate) Item.get(Item.LEATHER_CHESTPLATE)).setColor(DyeColor.values()[random.nextInt(DyeColor.values().length)]))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.2f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.TURTLE_HELMET, 0, 4), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 6), Item.get(Item.LEATHER_HORSE_ARMOR))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .setPriceMultiplierA(0.2f)
                        .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 6), Item.get(Item.SADDLE))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(5)
                    .setTraderExp(30)
                    .setPriceMultiplierA(0.2f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 5), ((ItemLeatherHelmet) Item.get(Item.LEATHER_HELMET)).setColor(DyeColor.values()[random.nextInt(DyeColor.values().length)]))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(5)
                    .setTraderExp(30)
                    .setPriceMultiplierA(0.2f)
                    .build());
        }
        return recipes;
    }

}
