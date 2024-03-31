package cn.nukkit.entity.data.profession;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.TradeRecipeBuildUtils;

import java.util.Random;

public class ProfessionWeapon extends Profession {

    public ProfessionWeapon() {
        super(9, BlockID.GRINDSTONE, "entity.villager.weapon");
    }

    @Override
    public ListTag<CompoundTag> buildTrades(int seed) {
        ListTag<CompoundTag> recipes = new ListTag<>();
        Random random = new Random(seed);

        int[] enchantments = new int[] {Enchantment.ID_DURABILITY, Enchantment.ID_DAMAGE_ALL, Enchantment.ID_VANISHING_CURSE, Enchantment.ID_DAMAGE_SMITE, Enchantment.ID_DAMAGE_ARTHROPODS, Enchantment.ID_LOOTING, Enchantment.ID_FIRE_ASPECT};

        Item ironsword = Item.get(Item.IRON_SWORD);
        Enchantment ironswordEnchantment = Enchantment.getEnchantment(enchantments[random.nextInt(enchantments.length)]);
        ironswordEnchantment.setLevel(1 + random.nextInt(ironswordEnchantment.getMaxLevel()));
        ironsword.addEnchantment(ironswordEnchantment);

        Item diamondAxe = Item.get(Item.DIAMOND_AXE);
        Enchantment diamondAxeEnchantment = Enchantment.getEnchantment(enchantments[random.nextInt(enchantments.length)]);
        diamondAxeEnchantment.setLevel(1 + random.nextInt(diamondAxeEnchantment.getMaxLevel()));
        diamondAxe.addEnchantment(diamondAxeEnchantment);

        Item diamondsword = Item.get(Item.DIAMOND_SWORD);
        Enchantment diamondswordEnchantment = Enchantment.getEnchantment(enchantments[random.nextInt(enchantments.length)]);
        diamondswordEnchantment.setLevel(1 + random.nextInt(diamondswordEnchantment.getMaxLevel()));
        diamondsword.addEnchantment(diamondswordEnchantment);

            recipes.add(TradeRecipeBuildUtils.of(Item.get(Item.COAL, 0 , 15), Item.get(Item.EMERALD))
                        .setMaxUses(16)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(2)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 3), Item.get(Item.IRON_AXE))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 7 + random.nextInt(22-7)), ironsword)
                        .setMaxUses(3)
                        .setRewardExp((byte) 1)
                        .setTier(1)
                        .setTraderExp(1)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.IRON_INGOT, 0 , 4), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(10)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 36), Item.get(BlockID.BELL))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(2)
                        .setTraderExp(5)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.FLINT, 0 , 24), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(3)
                        .setTraderExp(20)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.DIAMOND, 0 , 1), Item.get(Item.EMERALD))
                        .setMaxUses(12)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(30)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 17 + random.nextInt(32-17)), diamondAxe)
                        .setMaxUses(3)
                        .setRewardExp((byte) 1)
                        .setTier(4)
                        .setTraderExp(15)
                        .build())
                .add(TradeRecipeBuildUtils.of(Item.get(Item.EMERALD, 0 , 13 + random.nextInt(27-13)), diamondsword)
                        .setMaxUses(3)
                        .setRewardExp((byte) 1)
                        .setTier(5)
                        .setTraderExp(0)
                        .build());
            return recipes;
    }

}
