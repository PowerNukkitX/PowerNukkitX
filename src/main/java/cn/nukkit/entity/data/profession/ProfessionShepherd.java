package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

public class ProfessionShepherd extends Profession {

    public ProfessionShepherd() {
        super(3, BlockID.LOOM, "entity.villager.shepherd");
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);

        Item rod = Item.get(Item.FISHING_ROD);
        Enchantment rodEnchantment = Enchantment.getEnchantment(new int[]{Enchantment.ID_DURABILITY, Enchantment.ID_LURE, Enchantment.ID_FORTUNE_FISHING}[random.nextInt(2)]);
        rodEnchantment.setLevel(random.nextInt(3) + 1);
        rod.addEnchantment(rodEnchantment);

        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.WOOL, random.nextInt(16), 18), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 2), Item.get(Item.SHEARS))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.DYE, random.nextInt(5), 12), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.WOOL))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.CARPET))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.DYE, 5 + random.nextInt(5), 12), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 3), Item.get(BlockID.BED, DyeColor.BLUE.getItemDyeMeta()))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.DYE, 10 + random.nextInt(6), 12), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 1), Item.get(Item.BANNER))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 2), Item.get(Item.PAINTING))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(2)
                        .build());
        return recipes;
    }

}
