package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

public class ProfessionFarmer extends Profession {

    public ProfessionFarmer() {
        super(1, BlockID.COMPOSTER, "entity.villager.farmer", Sound.BLOCK_COMPOSTER_FILL);
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);
        switch (random.nextInt(4)) {
            case 0:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(BlockID.WHEAT, 0, 20), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 1:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(BlockID.BEETROOT, 0, 15), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 2:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.CARROT, 0, 22), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 3:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.POTATO, 0, 26), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .setPriceMultiplierA(0.05f)
                        .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.BREAD, 0, 6))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(BlockID.PUMPKIN, 0, 6), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.PUMPKIN_PIE, 0, 4))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(2)
                    .setTraderExp(5)
                    .setPriceMultiplierA(0.05f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.APPLE, 0, 4))
                    .setMaxUses(16)
                    .setRewardExp((byte) 1)
                    .setTier(2)
                    .setTraderExp(5)
                    .setPriceMultiplierA(0.05f)
                    .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get(BlockID.MELON_BLOCK, 0, 4), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), Item.get(Item.COOKIE, 0, 18))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.SUSPICIOUS_STEW, random.nextInt(6)))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(BlockID.CAKE))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), Item.get(Item.GOLDEN_CARROT, 0, 3))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(30)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 4), Item.get(Item.GLISTERING_MELON_SLICE, 0, 3))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(30)
                        .setPriceMultiplierA(0.05f)
                        .build());
        return recipes;
    }

}
