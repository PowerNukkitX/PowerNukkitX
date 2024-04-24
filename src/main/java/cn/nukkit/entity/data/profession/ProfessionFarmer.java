package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TradeRecipeBuildUtils;

public class ProfessionFarmer extends Profession {

    public ProfessionFarmer() {
        super(1, BlockID.COMPOSTER, "entity.villager.farmer");
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();

        recipes.add(TradeRecipeBuildUtils.of(Item.get(BlockID.WHEAT,0,20), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(BlockID.BEETROOT,0,15), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.CARROT,0,22), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.POTATO,0,26), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.BREAD,0,6))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(BlockID.PUMPKIN,0,6), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.PUMPKIN_PIE,0,4))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.APPLE,0,4))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(BlockID.MELON_BLOCK, 0, 4), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), Item.get(Item.COOKIE, 0 ,18))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.SUSPICIOUS_STEW))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(BlockID.CAKE))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), Item.get(Item.GOLDEN_CARROT,0, 3))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 4), Item.get(Item.GLISTERING_MELON_SLICE, 0 ,3))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build());
            return recipes;
    }

}
