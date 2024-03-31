package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.*;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TradeRecipeBuildUtils;

public class ProfessionLeather extends Profession {

    public ProfessionLeather() {
        super(12, BlockID.CAULDRON, "entity.villager.leather");
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();

        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.LEATHER, 0, 6), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 7), ((ItemLeatherChestplate) Item.get(Item.LEATHER_CHESTPLATE)).setColor(DyeColor.RED))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), ((ItemLeatherLeggings) Item.get(Item.LEATHER_LEGGINGS)).setColor(DyeColor.RED))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.FLINT, 0, 26), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 5), ((ItemLeatherHelmet) Item.get(Item.LEATHER_HELMET)).setColor(DyeColor.RED))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 4), ((ItemLeatherBoots) Item.get(Item.LEATHER_BOOTS)).setColor(DyeColor.RED))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.RABBIT_HIDE, 0, 9), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 7), ((ItemLeatherChestplate) Item.get(Item.LEATHER_CHESTPLATE)).setColor(DyeColor.RED))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.TURTLE_HELMET, 0, 4), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 6), Item.get(Item.LEATHER_HORSE_ARMOR))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 6), Item.get(Item.SADDLE))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 5), ((ItemLeatherHelmet) Item.get(Item.LEATHER_HELMET)).setColor(DyeColor.RED))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build());
        return recipes;
    }

}
