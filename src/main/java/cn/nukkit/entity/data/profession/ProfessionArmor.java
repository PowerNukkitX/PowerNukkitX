package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

public class ProfessionArmor extends Profession {

    public ProfessionArmor() {
        super(8, BlockID.BLAST_FURNACE, "entity.villager.armor");
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);

        int[] enchantments = new int[] {Enchantment.ID_DURABILITY, Enchantment.ID_THORNS, Enchantment.ID_PROTECTION_ALL, Enchantment.ID_PROTECTION_EXPLOSION, Enchantment.ID_PROTECTION_PROJECTILE, Enchantment.ID_PROTECTION_FIRE, Enchantment.ID_VANISHING_CURSE};
        Item diamondLeggings = Item.get(Item.DIAMOND_LEGGINGS);
        Enchantment diamondLeggingsEnchantment = Enchantment.getEnchantment(enchantments[random.nextInt(enchantments.length)]);
        diamondLeggingsEnchantment.setLevel(1 + random.nextInt(diamondLeggingsEnchantment.getMaxLevel()));
        diamondLeggings.addEnchantment(diamondLeggingsEnchantment);

        Item diamondChestplate= Item.get(Item.DIAMOND_CHESTPLATE);
        Enchantment diamondChestplateEnchantment = Enchantment.getEnchantment(enchantments[random.nextInt(enchantments.length)]);
        diamondChestplateEnchantment.setLevel(1 + random.nextInt(diamondChestplateEnchantment.getMaxLevel()));
        diamondChestplate.addEnchantment(diamondChestplateEnchantment);

        Item diamondHelmet = Item.get(Item.DIAMOND_HELMET);
        Enchantment diamondHelmetEnchantment = Enchantment.getEnchantment(enchantments[random.nextInt(enchantments.length)]);
        diamondHelmetEnchantment.setLevel(1 + random.nextInt(diamondHelmetEnchantment.getMaxLevel()));
        diamondHelmet.addEnchantment(diamondHelmetEnchantment);

        Item diamondBoots = Item.get(Item.DIAMOND_BOOTS);
        Enchantment diamondBootsEnchantment = Enchantment.getEnchantment(enchantments[random.nextInt(enchantments.length)]);
        diamondBootsEnchantment.setLevel(1 + random.nextInt(diamondBootsEnchantment.getMaxLevel()));
        diamondBoots.addEnchantment(diamondBootsEnchantment);

            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.COAL,0,15), Item.get(Item.EMERALD, 0 ,1))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,4), Item.get(Item.IRON_BOOTS, 0 ,1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,7), Item.get(Item.IRON_LEGGINGS, 0 ,1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,5), Item.get(Item.IRON_HELMET, 0 ,1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,9), Item.get(Item.IRON_CHESTPLATE, 0 ,1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.IRON_INGOT,0,4), Item.get(Item.EMERALD, 0 ,1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,36), Item.get(BlockID.BELL, 0 ,1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,3), Item.get(Item.CHAINMAIL_LEGGINGS, 0 ,1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,1), Item.get(Item.CHAINMAIL_BOOTS, 0 ,1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.LAVA_BUCKET), Item.get(Item.EMERALD, 0 ,1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.DIAMOND,0,1), Item.get(Item.EMERALD, 0 ,1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,4), Item.get(Item.CHAINMAIL_CHESTPLATE, 0 ,1))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(10)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,1), Item.get(Item.CHAINMAIL_HELMET, 0 ,1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,5), Item.get(Item.SHIELD, 0 ,1))
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,19 + random.nextInt(34-19)), diamondLeggings)
                        .setMaxUses(3)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,13 + random.nextInt(28-13)), diamondBoots)
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,21 + random.nextInt(36-21)), diamondChestplate)
                        .setMaxUses(3)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD,0,13 + random.nextInt(28-13)), diamondHelmet)
                        .setMaxUses(99)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build());
            return recipes;
    }

}
