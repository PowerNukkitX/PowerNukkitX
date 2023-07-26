package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.*;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.RecipeBuildUtils;

public class ProfessionLeather extends Profession {

    public ProfessionLeather() {
        super(12, BlockID.CAULDRON_BLOCK, "entity.villager.leather");
    }

    @Override
    public ListTag<Tag> buildTrades(int seed) {
        ListTag<Tag> recipes = new ListTag<>("Recipes");

        recipes.add(RecipeBuildUtils.of(MinecraftItemID.LEATHER.get(6), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(
                                Item.get(Item.EMERALD, 0, 7),
                                ((ItemChestplateLeather) Item.get(Item.LEATHER_TUNIC)).setColor(DyeColor.RED))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .build())
                .add(RecipeBuildUtils.of(
                                Item.get(Item.EMERALD, 0, 3),
                                ((ItemLeggingsLeather) Item.get(Item.LEATHER_PANTS)).setColor(DyeColor.RED))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(0)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.FLINT, 0, 26), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .build())
                .add(RecipeBuildUtils.of(
                                Item.get(Item.EMERALD, 0, 5),
                                ((ItemHelmetLeather) Item.get(Item.LEATHER_CAP)).setColor(DyeColor.RED))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .build())
                .add(RecipeBuildUtils.of(
                                Item.get(Item.EMERALD, 0, 4),
                                ((ItemBootsLeather) Item.get(Item.LEATHER_BOOTS)).setColor(DyeColor.RED))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(0)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.RABBIT_HIDE, 0, 9), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .build())
                .add(RecipeBuildUtils.of(
                                Item.get(Item.EMERALD, 0, 7),
                                ((ItemChestplateLeather) Item.get(Item.LEATHER_TUNIC)).setColor(DyeColor.RED))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.TURTLE_SHELL, 0, 4), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 6), Item.get(Item.LEATHER_HORSE_ARMOR))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 6), Item.get(Item.SADDLE))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build())
                .add(RecipeBuildUtils.of(
                                Item.get(Item.EMERALD, 0, 5),
                                ((ItemHelmetLeather) Item.get(Item.LEATHER_CAP)).setColor(DyeColor.RED))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build());
        return recipes;
    }
}
