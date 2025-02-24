package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

public class ProfessionLibrarian extends Profession {

    public ProfessionLibrarian() {
        super(5, BlockID.LECTERN, "entity.villager.librarian", Sound.ITEM_BOOK_PUT);
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);

        Item book1 = Item.get(Item.ENCHANTED_BOOK);
        Enchantment e_book1 = Enchantment.getEnchantments()[random.nextInt(Enchantment.getEnchantments().length)];
        e_book1.setLevel(random.nextInt(e_book1.getMaxLevel()) + 1);
        book1.addEnchantment(e_book1);
        Item book2 = Item.get(Item.ENCHANTED_BOOK);
        Enchantment e_book2 = Enchantment.getEnchantments()[random.nextInt(Enchantment.getEnchantments().length)];
        e_book2.setLevel(random.nextInt(e_book2.getMaxLevel()) + 1);
        book2.addEnchantment(e_book2);
        Item book3 = Item.get(Item.ENCHANTED_BOOK);
        Enchantment e_book3 = Enchantment.getEnchantments()[random.nextInt(Enchantment.getEnchantments().length)];
        e_book3.setLevel(random.nextInt(e_book3.getMaxLevel()) + 1);
        book3.addEnchantment(e_book3);
        Item book4 = Item.get(Item.ENCHANTED_BOOK);
        Enchantment e_book4 = Enchantment.getEnchantments()[random.nextInt(Enchantment.getEnchantments().length)];
        e_book4.setLevel(random.nextInt(e_book4.getMaxLevel()) + 1);
        book4.addEnchantment(e_book4);

        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.PAPER, 0, 24), Item.get(Item.EMERALD))
                .setMaxUses(16)
                .setRewardExp((byte) 1)
                .setTier(1)
                .setTraderExp(2)
                .setPriceMultiplierA(0.05f)
                .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 9), Item.get(BlockID.BOOKSHELF))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(1)
                    .setTraderExp(1)
                    .setPriceMultiplierA(0.05f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 5 + random.nextInt(60)), Item.get(Item.BOOK), book1)
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(1)
                    .setTraderExp(1)
                    .setPriceMultiplierA(0.2f)
                    .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.BOOK), Item.get(Item.EMERALD))
                .setMaxUses(12)
                .setRewardExp((byte) 1)
                .setTier(2)
                .setTraderExp(10)
                .setPriceMultiplierA(0.05f)
                .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(BlockID.LANTERN))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(2)
                    .setTraderExp(5)
                    .setPriceMultiplierA(0.05f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 5 + random.nextInt(60)), Item.get(Item.BOOK), book2)
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(2)
                    .setTraderExp(5)
                    .setPriceMultiplierA(0.2f)
                    .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.INK_SAC), Item.get(Item.EMERALD))
                .setMaxUses(12)
                .setRewardExp((byte) 1)
                .setTier(3)
                .setTraderExp(20)
                .setPriceMultiplierA(0.05f)
                .build());
        if (random.nextBoolean()) {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD), Item.get(BlockID.GLASS, 0, 4))
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(3)
                    .setTraderExp(10)
                    .setPriceMultiplierA(0.05f)
                    .build());
        } else {
            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 5 + random.nextInt(60)), Item.get(Item.BOOK), book3)
                    .setMaxUses(12)
                    .setRewardExp((byte) 1)
                    .setTier(3)
                    .setTraderExp(10)
                    .setPriceMultiplierA(0.2f)
                    .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.WRITABLE_BOOK), Item.get(Item.WRITABLE_BOOK), Item.get(Item.EMERALD))
                .setMaxUses(12)
                .setRewardExp((byte) 1)
                .setTier(4)
                .setTraderExp(30)
                .setPriceMultiplierA(0.05f)
                .build());
        switch (random.nextInt(3)) {
            case 0:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 5), Item.get(Item.CLOCK))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 1:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 4), Item.get(Item.COMPASS))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .setPriceMultiplierA(0.05f)
                        .build());
                break;
            case 2:
                recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 5 + random.nextInt(60)), Item.get(Item.BOOK), book4)
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .setPriceMultiplierA(0.2f)
                        .build());
        }
        recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0, 12), Item.get(Item.NAME_TAG))
                .setMaxUses(12)
                .setRewardExp((byte) 1)
                .setTier(5)
                .setTraderExp(30)
                .setPriceMultiplierA(0.05f)
                .build());
        return recipes;
    }

}
