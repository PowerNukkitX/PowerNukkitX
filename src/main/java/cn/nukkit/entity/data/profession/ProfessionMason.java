package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

public class ProfessionMason extends Profession {

    public ProfessionMason() {
        super(13, BlockID.STONECUTTER_BLOCK, "entity.villager.mason", Sound.BLOCK_STONECUTTER_USE);
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);

        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.CLAY_BALL, 0, 10), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.BRICK, 0, 10))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(BlockID.STONE, 0, 20), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(BlockID.CHISELED_STONE_BRICKS, 0, 4))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .setPriceMultiplierA(0.05f)
                        .build());
        switch (random.nextInt(3)) {
            case 0:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(BlockID.GRANITE, 0, 16), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 1:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(BlockID.ANDESITE, 0, 16), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 2:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(BlockID.DIORITE, 0, 16), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .setPriceMultiplierA(0.05f)
                        .build());
        }
        switch (random.nextInt(4)) {
            case 0:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(BlockID.POLISHED_ANDESITE, 0, 4))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 1:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(BlockID.POLISHED_DIORITE, 0, 4))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 2:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(BlockID.POLISHED_GRANITE, 0, 4))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 3:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(BlockID.DRIPSTONE_BLOCK, 0, 1))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .setPriceMultiplierA(0.05f)
                        .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.QUARTZ, 0, 12), Item.get(Item.EMERALD))
                .setMaxUses(12)
                .setRewardExp((byte) 1)
                .setTier(4)
                .setTraderExp(30)
                .setPriceMultiplierA(0.05f)
                .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get("minecraft:" + DyeColor.values()[random.nextInt(DyeColor.values().length)].name().toLowerCase().replace(" ", "_") + "_terracotta"))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(4)
                    .setTraderExp(15)
                    .setPriceMultiplierA(0.05f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get("minecraft:" + DyeColor.values()[random.nextInt(DyeColor.values().length)].name().toLowerCase().replace(" ", "_") + "_glazed_terracotta"))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(4)
                    .setTraderExp(15)
                    .setPriceMultiplierA(0.05f)
                    .build());
        }
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(BlockID.QUARTZ_PILLAR))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(5)
                    .setTraderExp(30)
                    .setPriceMultiplierA(0.05f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(BlockID.QUARTZ_BLOCK))
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
