package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.RecipeBuildUtils;
import java.util.Random;

public class ProfessionFisherman extends Profession {

    public ProfessionFisherman() {
        super(2, BlockID.BARREL, "entity.villager.fisherman");
    }

    @Override
    public ListTag<Tag> buildTrades(int seed) {
        ListTag<Tag> recipes = new ListTag<>("Recipes");
        Random random = new Random(seed);

        Item rod = MinecraftItemID.FISHING_ROD.get(1);
        Enchantment rodEnchantment = Enchantment.get(
                new int[] {Enchantment.ID_DURABILITY, Enchantment.ID_LURE, Enchantment.ID_FORTUNE_FISHING}
                        [random.nextInt(2)]);
        rodEnchantment.setLevel(random.nextInt(3) + 1);
        rod.addEnchantment(rodEnchantment);

        recipes.add(RecipeBuildUtils.of(Item.get(Item.STRING, 0, 20), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.COAL, 0, 10), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), MinecraftItemID.COD_BUCKET.get(1))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(
                                Item.get(Item.EMERALD), MinecraftItemID.COD.get(6), MinecraftItemID.COOKED_COD.get(6))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(MinecraftItemID.COD.get(15), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(MinecraftItemID.EMERALD.get(2), MinecraftItemID.CAMPFIRE.get(1))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(
                                Item.get(Item.EMERALD),
                                MinecraftItemID.SALMON.get(5),
                                MinecraftItemID.COOKED_SALMON.get(6))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(MinecraftItemID.SALMON.get(13), MinecraftItemID.EMERALD.get(1))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(MinecraftItemID.EMERALD.get(8 + random.nextInt(23 - 8)), rod)
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(MinecraftItemID.TROPICAL_FISH.get(6), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(MinecraftItemID.PUFFERFISH.get(4), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(2)
                        .build())
                .add(RecipeBuildUtils.of(MinecraftItemID.BOAT.get(1), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(2)
                        .build());
        return recipes;
    }
}
