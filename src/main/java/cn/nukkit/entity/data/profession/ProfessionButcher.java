package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.RecipeBuildUtils;

import java.util.Random;

public class ProfessionButcher extends Profession {

    public ProfessionButcher() {
        super(11, BlockID.SMOKER, "entity.villager.butcher");
    }

    @Override
    public ListTag<Tag> buildTrades(int seed) {
        ListTag<Tag> recipes = new ListTag<>("Recipes");

            recipes.add(RecipeBuildUtils.of(Item.get(Item.RAW_CHICKEN, 0 , 14), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.RAW_PORKCHOP, 0 , 7), Item.get(Item.EMERALD))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(0)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.RAW_RABBIT, 0 , 4), Item.get(Item.EMERALD))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(0)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 1), Item.get(Item.RABBIT_STEW))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.COAL, 0 , 15), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 1), Item.get(Item.COOKED_PORKCHOP , 0 , 5))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 1), Item.get(Item.COOKED_CHICKEN, 0 , 8))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(0)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.RAW_MUTTON, 0 , 7), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(30)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.RAW_BEEF, 0 , 10), Item.get(Item.EMERALD))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(0)
                        .build())
                .add(RecipeBuildUtils.of(MinecraftItemID.DRIED_KELP_BLOCK.get(10), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .build())
                .add(RecipeBuildUtils.of(MinecraftItemID.SWEET_BERRIES.get(10), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build());

            return recipes;
    }

}
