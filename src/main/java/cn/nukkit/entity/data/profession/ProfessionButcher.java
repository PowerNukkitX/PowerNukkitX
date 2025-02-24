package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

import static cn.nukkit.item.ItemID.SWEET_BERRIES;

public class ProfessionButcher extends Profession {

    public ProfessionButcher() {
        super(11, BlockID.SMOKER, "entity.villager.butcher", Sound.BLOCK_SMOKER_SMOKE);
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);
        switch (random.nextInt(3)) {
            case 0:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.CHICKEN, 0, 14), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 1:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.PORKCHOP, 0, 7), Item.get(Item.EMERALD))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 2:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.RABBIT, 0, 4), Item.get(Item.EMERALD))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .setPriceMultiplierA(0.05f)
                        .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.RABBIT_STEW))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.COAL, 0, 15), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.COOKED_PORKCHOP, 0, 5))
                    .setMaxUses(16)
                    .setRewardExp((byte) 1)
                    .setTier(2)
                    .setTraderExp(5)
                    .setPriceMultiplierA(0.05f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.COOKED_CHICKEN, 0, 8))
                    .setMaxUses(99)
                    .setRewardExp((byte) 1)
                    .setTier(2)
                    .setTraderExp(5)
                    .setPriceMultiplierA(0.05f)
                    .build());
        }
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.MUTTON, 0, 7), Item.get(Item.EMERALD))
                    .setMaxUses(16)
                    .setRewardExp((byte) 1)
                    .setTier(3)
                    .setTraderExp(20)
                    .setPriceMultiplierA(0.05f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.BEEF, 0, 10), Item.get(Item.EMERALD))
                    .setMaxUses(99)
                    .setRewardExp((byte) 1)
                    .setTier(3)
                    .setTraderExp(20)
                    .setPriceMultiplierA(0.05f)
                    .build());
        }

        recipes.add(TradeRecipeBuildUtils.of(Item.get(BlockID.DRIED_KELP_BLOCK, 0, 10), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(SWEET_BERRIES, 0, 10), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(30)
                        .setPriceMultiplierA(0.05f)
                        .build());
        return recipes;
    }

}
