package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.RecipeBuildUtils;
import java.util.Random;

public class ProfessionMason extends Profession {

    public ProfessionMason() {
        super(13, BlockID.STONECUTTER_BLOCK, "entity.villager.mason");
    }

    @Override
    public ListTag<Tag> buildTrades(int seed) {
        ListTag<Tag> recipes = new ListTag<>("Recipes");
        Random random = new Random(seed);

        recipes.add(RecipeBuildUtils.of(Item.get(Item.CLAY, 0, 10), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.BRICK, 0, 10))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.STONE, 0, 20), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(98, 3, 4))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.STONE, 0, 16), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .build())
                .add(RecipeBuildUtils.of(
                                Item.get(Item.EMERALD, 0, 1),
                                Item.get(Item.STONE, new int[] {2, 4, 6}[random.nextInt(3)], 4))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), MinecraftItemID.DRIPSTONE_BLOCK.get(1))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.QUARTZ, 0, 12), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.TERRACOTTA, 0, 1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.WHITE_GLAZED_TERRACOTTA, 0, 1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(0)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), MinecraftItemID.QUARTZ_BRICKS.get(1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.CONCRETE))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build());
        return recipes;
    }
}
