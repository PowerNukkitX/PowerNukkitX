package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

public class ProfessionFisherman extends Profession {

    public ProfessionFisherman() {
        super(2, BlockID.BARREL, "entity.villager.fisherman");
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);

        Item rod = Item.get(ItemID.FISHING_ROD);
        Enchantment rodEnchantment = Enchantment.getEnchantment(new int[]{Enchantment.ID_DURABILITY, Enchantment.ID_LURE, Enchantment.ID_FORTUNE_FISHING}[random.nextInt(2)]);
        rodEnchantment.setLevel(random.nextInt(3) + 1);
        rod.addEnchantment(rodEnchantment);

        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.STRING, 0, 20), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.COAL, 0, 10), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), Item.get(Item.COD_BUCKET, 0, 1))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.COD, 0, 6), Item.get(Item.COOKED_COD, 0, 6))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.COD, 0, 15), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 2), Item.get(BlockID.CAMPFIRE, 0, 1))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(Item.SALMON, 0, 5), Item.get(Item.COOKED_SALMON, 0, 6))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.SALMON, 0, 13), Item.get(Item.EMERALD, 0, 1))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 8 + random.nextInt(23 - 8)), rod)
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.TROPICAL_FISH, 0, 6), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.PUFFERFISH, 0, 4), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.BOAT, 0, 1), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(2)
                        .build());
        return recipes;
    }

}
