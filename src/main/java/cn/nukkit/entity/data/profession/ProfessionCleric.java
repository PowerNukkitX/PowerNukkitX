package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.RecipeBuildUtils;
import java.util.Random;

public class ProfessionCleric extends Profession {

    public ProfessionCleric() {
        super(7, BlockID.BREWING_STAND_BLOCK, "entity.villager.cleric");
    }

    @Override
    public ListTag<Tag> buildTrades(int seed) {
        ListTag<Tag> recipes = new ListTag<>("Recipes");
        Random random = new Random(seed);

        recipes.add(RecipeBuildUtils.of(Item.get(Item.ROTTEN_FLESH, 0, 32), Item.get(Item.EMERALD, 0, 2))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.REDSTONE_DUST, 0, 2))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.GOLD_INGOT, 0, 3), Item.get(Item.EMERALD, 0, 1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), MinecraftItemID.LAPIS_LAZULI.get(1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.RABBIT_FOOT, 0, 2), Item.get(Item.EMERALD, 0, 2))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 4), Item.get(Item.GLOWSTONE, 0, 1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.TURTLE_SHELL, 0, 4), Item.get(Item.EMERALD, 0, 1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.GLASS_BOTTLE, 0, 9), Item.get(Item.EMERALD, 0, 1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(0)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 5), Item.get(Item.ENDER_PEARL, 0, 1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.NETHER_WART, 0, 22), Item.get(Item.EMERALD, 0, 1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), Item.get(Item.EXPERIENCE_BOTTLE, 0, 1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build());
        return recipes;
    }
}
