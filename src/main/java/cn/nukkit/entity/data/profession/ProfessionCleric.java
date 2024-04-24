package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TradeRecipeBuildUtils;

public class ProfessionCleric extends Profession {

    public ProfessionCleric() {
        super(7, BlockID.BREWING_STAND, "entity.villager.cleric");
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();

        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.ROTTEN_FLESH,0,32), Item.get(Item.EMERALD, 0 , 2))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,1), Item.get(Item.REDSTONE, 0, 2))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.GOLD_INGOT,0,3), Item.get(Item.EMERALD, 0, 1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,1), Item.get(Item.LAPIS_LAZULI))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.RABBIT_FOOT,0,2), Item.get(Item.EMERALD, 0, 2))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,4), Item.get(BlockID.GLOWSTONE, 0, 1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.TURTLE_HELMET,0,4), Item.get(Item.EMERALD, 0, 1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.GLASS_BOTTLE,0,9), Item.get(Item.EMERALD, 0, 1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,5), Item.get(Item.ENDER_PEARL, 0, 1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(BlockID.NETHER_WART,0,22), Item.get(Item.EMERALD, 0, 1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,3), Item.get(Item.EXPERIENCE_BOTTLE, 0, 1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build());
        return recipes;
    }

}
